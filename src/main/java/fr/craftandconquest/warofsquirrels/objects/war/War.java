package fr.craftandconquest.warofsquirrels.objects.war;

import fr.craftandconquest.warofsquirrels.objects.city.CityRank;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import fr.craftandconquest.warofsquirrels.objects.city.CityRank;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Scoreboard;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class War {
    public enum WarState{
        Declaration,
        Preparation,
        War,
        Rollback
    }

    private String          _tag;
    private City _cityAttacker;
    private City            _cityDefender;
    private List<DBPlayer>  _attackers;
    private List<DBPlayer>  _defenders;
    private Task            _timer;
    private WarState        _state;
    private int             _attackersLimit;
    private long            _timeStart;
    private DBPlayer        _target;
    private boolean         _targetDead = false;
    private int             _attackerPoints = 0;
    private int             _defenderPoints = 0;

    private int             lastAnnouceCapture = 0;
    private float           _vitesseCapture;

    private ConfigurationNode   _node;

    private List<Transaction<BlockSnapshot>>    _rollbackBlocks;
    private List<Chunk>                         _capturedChunk = new ArrayList<>();
    private Map<Chunk, Float>                 _inCaptureChunk = new HashMap<>();

    public          War(City attacker, City defender, List<DBPlayer> attackersList, ConfigurationNode node) {
        _cityAttacker = attacker;
        _cityDefender = defender;
        _target = null;
        _defenders = new ArrayList<>();
        Core.getCityHandler().getOnlineDBPlayers(defender).forEach(this::addDefender);
        _attackersLimit = _defenders.size() + 1;
        _attackers = new ArrayList<>();
        attackersList.forEach(this::addAttacker);
        _rollbackBlocks = new ArrayList<>();
        _state = WarState.Preparation;
        _tag = setTag();
        _node = node.getNode(_tag);
        Core.Send(Text.of(TextColors.GOLD, "Une attaque vient d'être lancé ! ", TextColors.RED, _cityAttacker.getDisplayName(),
                TextColors.GOLD, " attaque ",
                TextColors.BLUE, _cityDefender.getDisplayName(), TextColors.RESET));
        setTarget();
        launchPreparation();
        setScoreboard();
    }

    public void         setScoreboard() {
        _attackers.forEach(this::displayScoreboard);
    }

    private void        displayScoreboard(DBPlayer player) {
        List<Objective> objs = new ArrayList<>();

        Objective test = Objective.builder()
                .displayName(Text.of("[" + _tag + "]"))
                .name("Name")
                .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                .criterion(Criteria.DUMMY)
                .build();

        objs.add(test);

        player.getUser().getPlayer().get().setScoreboard(Scoreboard.builder()
                .objectives(objs)
                .build());
    }

    public void         setTarget(DBPlayer player) { this._target = player; }

    public void        setTarget() {
        _defenders.forEach(defender -> {
            if (defender.getCity().getOwner() == defender) {
                _target = defender;
                return;
            } else if (defender.isAssistant() && _target == null) {
                _target = defender;
            }
        });

        if (_target == null)
            _target = _defenders.get(0);
    }

    private String  setTag() {
        return (_cityAttacker.getDisplayName().substring(0, 3)
                + _cityDefender.getDisplayName().substring(0, 3));
    }

    public boolean      addAttacker(DBPlayer player) {
        Player attacker = player.getUser().getPlayer().get();
        if (_attackers.size() == _attackersLimit) {
            player.sendMessage(Text.of("You can't join this war, wait for defenders to join"));
            return false;
        }
        _attackers.add(player);
        attacker.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, player.getDisplayName(), TextColors.RESET));
        Core.getBroadcastHandler().warAnnounce(this, player.getDisplayName() +
                " join as Attacker ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public boolean      addDefender(DBPlayer player) {
        Player defender = player.getUser().getPlayer().get();
        _defenders.add(player);
        ++_attackersLimit;
        defender.offer(Keys.DISPLAY_NAME, Text.of(TextColors.BLUE, player.getDisplayName(), TextColors.RESET));
        Core.getBroadcastHandler().warAnnounce(this, player.getDisplayName() +
                " join as Defender ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public void         addRollbackBlock(Transaction<BlockSnapshot> transaction) {
        int size = _rollbackBlocks.size();
        ConfigurationNode       rb = _node.getNode("Rollback" + size);
        BlockSnapshot           block = transaction.getOriginal();

        if (_rollbackBlocks.stream().anyMatch(
                t -> t.getOriginal().getPosition().equals(transaction.getOriginal().getPosition()))) return;

        Utils.replaceContainer(block);

        rb.getNode("world").setValue(block.getWorldUniqueId().toString());
        rb.getNode("x").setValue(block.getPosition().getX());
        rb.getNode("y").setValue(block.getPosition().getY());
        rb.getNode("z").setValue(block.getPosition().getZ());
        rb.getNode("type").setValue(block.getState().getType().toString());

        try {
            Core.getWarHandler().getManager().save(_node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        _rollbackBlocks.add(transaction);
        Core.getLogger().info("[Rollback][" + size + "->" + _rollbackBlocks.size() + "] "
                + transaction.getOriginal().getState().getType().toString()
                + " into "
                + transaction.getFinal().getState().getType().toString());
    }

    void                addDefenderPoints(int points) {
        this._defenderPoints += points;
    }

    public void         addDefenderKillPoints() { this._defenderPoints += 166 / (3 * _attackers.size()); }
    public void         addAttackerKillPoints() {
        this._attackerPoints += 166 / (3 * _defenders.size());
    }

    public void         addAttackerPointsTarget() {
        if (!_targetDead) {
            this._attackerPoints += 650;
            _targetDead = true;
        } else
            addAttackerKillPoints();
    }

    private int          addAttackerCapturePoints() {
        CityRank cityRank = Core.getInfoCityMap().get(this._cityDefender).getCityRank();
        int             max = cityRank.getChunkMax();
        int             used = Core.getChunkHandler().get(this._cityDefender).size();
        int             resultat = Math.round( max <= 15 ? 1000 / used : 67 * (max / used));

        Core.getBroadcastHandler().warAnnounce(this, "Un chunk à été capturé pour un total de " + resultat + " points.");
        _attackerPoints += resultat;
        return  resultat;
    }

    private void        launchPreparation() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.Preparation);
        this._timer = builder.delay(Core.getConfig().getPreparationPhase(), TimeUnit.SECONDS)
                .name(_tag + " preparation timer")
                .execute(() -> {
                    this._state = WarState.War;
                    this.launchWar();
        }).submit(Core.getMain());
    }

    private void        launchWar() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();
        WarTask         task;

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.War);
        task = new WarTask();
        task.setWar(this);
        this.capture();
        this._timer = builder.execute(task)
                .interval(1, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    void         lauchRollback() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.Rollback);
        this._timer = builder.execute(() -> {
            this.rollback();
            Core.getWarHandler().delete(this, _node);
        })
                .delay(Core.getConfig().getRollbackPhase(), TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    private void        rollback() {
        for (Transaction<BlockSnapshot> t : _rollbackBlocks)
            t.getOriginal().restore(true, BlockChangeFlags.ALL);
    }

    boolean      checkWin() {
        if (_defenderPoints >= 1000)
            Core.Send(_cityAttacker.getDisplayName() + " fail to win his attack against "
                    + _cityDefender.getDisplayName() + " (" + _attackerPoints + " to " + _defenderPoints + ")");
        else if (_attackerPoints >= 1000)
            Core.Send(_cityAttacker.getDisplayName() + " win his attack against "
                    + _cityDefender.getDisplayName() + " (" + _attackerPoints + " to " + _defenderPoints + ")");
        else
            return false;
        this._state = WarState.Rollback;
        return true;
    }

    public boolean      contains(DBPlayer player) {
        return (_attackers.contains(player) || _defenders.contains(player));
    }

    public boolean      contains(City city) {
        return (_cityAttacker == city || _cityDefender == city);
    }

    private String       timeLeft() {
        long            time = System.currentTimeMillis();
        long            timeLeft, delta;
        double          elapsedSeconds;
        int             minutes, seconds;

        String          res = "";

        delta = time - _timeStart;
        if (_state == WarState.Preparation)
            timeLeft = (long) ((Core.getConfig().getPreparationPhase()) * 1000.0);
        else if (_state == WarState.War)
            timeLeft = (long) ((30.0 * 60.0) * 1000.0);
        else
            timeLeft = (long) (Core.getConfig().getRollbackPhase() * 1000.0);
        timeLeft = timeLeft - delta;
        elapsedSeconds = timeLeft / 1000.0;
        minutes = (int) (elapsedSeconds / 60);
        seconds = (int) (elapsedSeconds % 60);
        if (minutes > 0)
            res = minutes + " min ";
        res += seconds + " s";

        return res;
    }

    public void     forceWinAttacker() {
        this._attackerPoints = 1000;
    }

    public void     forceWinDefender() {
        this._defenderPoints = 1000;
    }

    public String   getPhase() {
        if (_state == WarState.Preparation)
            return ("Preparation");
        else if (_state == WarState.War)
            return ("war");
        else
            return ("Rollback");
    }

    public List<DBPlayer>     getProtagonists() {
        List<DBPlayer>        list = new ArrayList<>(_defenders);

        list.addAll(_attackers);
        return list;
    }

    public boolean          removePlayer(DBPlayer player) {
        City                city = player.getCity();

        if (_attackers.contains(player))
            _attackers.remove(player);
        else if (_defenders.contains(player) && city != _cityDefender) {
            _defenders.remove(player);
            --_attackersLimit;
        }
        else
            return false;
        player.getUser().getPlayer().get().offer(Keys.DISPLAY_NAME, Text.of(TextColors.WHITE, player.getDisplayName(), TextColors.RESET));
        Core.getBroadcastHandler().warAnnounce(this, player.getDisplayName() +
                " left the war [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public boolean          isTarget(DBPlayer victim) { return _target.equals(victim); }
    public boolean          isAttacker(DBPlayer player) { return _attackers.contains(player); }
    public boolean          isDefender(DBPlayer player) { return _defenders.contains(player); }
    public City             getAttacker() { return _cityAttacker; }
    public City             getDefender() { return _cityDefender; }
    public int              getAttackerPoints() { return _attackerPoints; }
    public int              getDefenderPoints() { return _defenderPoints; }

    public List<DBPlayer>   getAttackers() { return _attackers; }
    public List<String>     getAttackersAsString() {
        List<String>        list = new ArrayList<>();

        _attackers.forEach(a -> list.add(a.getDisplayName()));
        return list;
    }

    public List<DBPlayer>   getDefenders() { return _defenders; }
    public List<String>     getDefendersAsString() {
        List<String>        list = new ArrayList<>();

        _defenders.forEach(d -> list.add(d.getDisplayName()));
        return list;
    }

    public void     displayInfo(DBPlayer player) {
        player.sendMessage(Text.of("===| " + this._tag + " |==="));
        player.sendMessage(Text.of(TextColors.RED, "Attackers [" + _cityAttacker.getDisplayName() + "] : " + Utils.getStringFromPlayerList(_attackers), TextColors.RESET));
        player.sendMessage(Text.of(TextColors.BLUE, "Defenders [" + _cityDefender.getDisplayName() + "] : " + Utils.getStringFromPlayerList(_defenders), TextColors.RESET));
        player.sendMessage(Text.of(TextColors.GOLD, "\nTarget : " + this._target.getDisplayName(), TextColors.RESET));
        player.sendMessage(Text.of("Phase : " + this.getPhase() + " (time left : " + timeLeft() + ")"));
        player.sendMessage(Text.of(TextStyles.BOLD, "\n===[" + _cityAttacker.getTag() + "] " + _attackerPoints + " - " + _defenderPoints + " [" + _cityDefender.getTag() + "]===", TextStyles.RESET));
        _inCaptureChunk.forEach((chunk, f) -> {
            float val = capture(chunk);

            if (val > 0f)
                player.sendMessage(Text.of("["
                    + (chunk.getPosX() * 16) + ";"
                    + (chunk.getPosZ() * 16) + "] "
                    + Utils.toTime((int) (_inCaptureChunk.get(chunk) / val))
                    + " secondes."));

        });
    }

    private void        capture() {
        int             chunkMax = Core.getInfoCityMap().get(_cityDefender).getCityRank().getChunkMax() - 1;
        float           t = (chunkMax <= 15.0f ? 900.0f/chunkMax : 60f);

        this._vitesseCapture = (100.0f / (_attackers.size() * t));
    }

    private float       capture(Chunk chunk) {
        World           world;
        int             att = 0, def = 0, x, z;
        float           ret;
        Chunk           c;
        List<DBPlayer>  list = new ArrayList<>();

        list.addAll(_attackers);
        list.addAll(_defenders);


        for (DBPlayer player : list) {
            if (player.getUser().isOnline()) {
                world = player.getUser().getPlayer().get().getWorld();
                x = player.getLastChunkX();
                z = player.getLastChunkZ();

                c = Core.getChunkHandler().get(x, z, world);
                if (c != null && c == chunk) {
                    if (isAttacker(player))
                        att++;
                    else if (isDefender(player))
                        def++;
                }
            }
        }

        ret = ((_vitesseCapture * att) - (0.45F * _vitesseCapture * def));
        Core.getLogger().warn("Ret : (" + _vitesseCapture + " * " + att + ") - (0.45 * " + _vitesseCapture + " * " + def + ") = " + ret);
        return ret;
    }

    void         updateCapture() {
        List<Chunk>         updated = new ArrayList<>();
        World               world;
        float               v;

        for (DBPlayer att : _attackers) {
            if (att.getUser().isOnline()) {
                world = att.getUser().getPlayer().get().getWorld();
                Chunk chunk = Core.getChunkHandler().get(att.getLastChunkX(), att.getLastChunkZ(), world);

                if (chunk != null && chunk.getCity() == _cityDefender
                        && !updated.contains(chunk) && !_capturedChunk.contains(chunk)
                        && !chunk.isHomeblock() && !chunk.isOutpost()) {
                    updated.add(chunk);
                    if (_inCaptureChunk.containsKey(chunk)) {
                        v = _inCaptureChunk.get(chunk);
                        _inCaptureChunk.compute(chunk, (c, val) -> val - capture(chunk));
                    } else {
                        v = 100;
                        _inCaptureChunk.put(chunk, 100 - capture(chunk));
                    }
                    if (this.lastAnnouceCapture == 0)
                        Core.getBroadcastHandler().warAnnounce(this, "[Capture][" + (chunk.getPosX() * 16) + ";" + (chunk.getPosZ() * 16) + "] Temps restant avant capture " + Utils.toTime((int) (_inCaptureChunk.get(chunk) / (v - _inCaptureChunk.get(chunk)))) + " secondes.");
                    if (_inCaptureChunk.get(chunk) <= 0) {
                        _capturedChunk.add(chunk);
                        _inCaptureChunk.remove(chunk);
                        addAttackerCapturePoints();
                    }
                }
            }
        }
        if (this.lastAnnouceCapture == 0)
            this.lastAnnouceCapture = 30;
        this.lastAnnouceCapture -= 1;
    }
}
