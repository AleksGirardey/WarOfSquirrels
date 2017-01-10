package fr.AleksGirardey.Objects.War;

import fr.AleksGirardey.Objects.City.Rank;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.BlockChangeFlag;

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
    private City            _cityAttacker;
    private City            _cityDefender;
    private List<DBPlayer>  _attackers;
    private List<DBPlayer>  _defenders;
    private Task            _timer;
    private WarState        _state;
    private int             _attackersLimit;
    private long            _timeStart;
    private DBPlayer        _target;
    private int             _attackerPoints = 0;
    private int             _defenderPoints = 0;

    private float           _vitesseCapture;

    private ConfigurationNode   _node;

    private List<Transaction<BlockSnapshot>>    _rollbackBlocks;
    private List<Chunk>                         _capturedChunk = new ArrayList<>();
    private Map<Chunk, Float>                 _inCaptureChunk = new HashMap<>();

    public          War(City attacker, City defender, List<DBPlayer> attackersList, ConfigurationNode node) {
        _cityAttacker = attacker;
        _cityDefender = defender;
        _target = null;
        _attackers = new ArrayList<>(attackersList);
        _defenders = new ArrayList<>(Core.getCityHandler().getOnlineDBPlayers(defender));
        _rollbackBlocks = new ArrayList<>();
        _attackersLimit = _defenders.size() + 1;
        _state = WarState.Preparation;
        _tag = setTag();
        _node = node.getNode(_tag);
        Core.Send("A war is about to start ! " + _cityAttacker.getDisplayName()
                + " attack " + _cityDefender.getDisplayName() + " !");
        setTarget();
        launchPreparation();
    }

    public void         setTarget(DBPlayer player) { this._target = player; }

    private void        setTarget() {
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
        if (_attackers.size() == _attackersLimit) {
            player.sendMessage(Text.of("You can't join this war, wait for defenders to join"));
            return false;
        }
        _attackers.add(player);
        Core.getBroadcastHandler().warAnnounce(this, player.getDisplayName() +
                " join as Attacker ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public boolean      addDefender(DBPlayer player) {
        _defenders.add(player);
        ++_attackersLimit;
        Core.getBroadcastHandler().warAnnounce(this, player.getDisplayName() +
                " join as Defender ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public void         addRollbackBlock(Transaction<BlockSnapshot> transaction) {
        int size = _rollbackBlocks.size();
        ConfigurationNode       rb = _node.getNode("Rollback" + size);
        BlockSnapshot           block = transaction.getOriginal();

        rb.getNode("world").setValue(block.getWorldUniqueId().toString());
        rb.getNode("x").setValue(block.getPosition().getX());
        rb.getNode("y").setValue(block.getPosition().getY());
        rb.getNode("z").setValue(block.getPosition().getZ());
        rb.getNode("type").setValue(block.getState().getType().toString());
        _rollbackBlocks.add(transaction);
        Core.getLogger().info("[Rollback][" + size + "->" + _rollbackBlocks.size() + "] "
                + transaction.getOriginal().getState().getType().toString()
                + " into "
                + transaction.getFinal().getState().getType().toString());
    }

    void                addDefenderPoints(int points) {
        this._defenderPoints += points;
    }

    public void         addDefenderKillPoints() {
        this._defenderPoints += 166 / (5 * _attackers.size());
    }

    public void         addAttackerKillPoints() {
        this._attackerPoints += 166 / (5 * _defenders.size());
    }

    public void         addAttackerPointsTarget() {
        this._attackerPoints += 650;
    }

    private int          addAttackerCapturePoints() {
        Rank            rank = Core.getInfoCityMap().get(this._cityDefender).getRank();
        int             max = rank.getChunkMax();
        int             used = Core.getChunkHandler().get(this._cityDefender).size();
        int             resultat = Math.round( max <= 15 ? 1000 / used : 67 * (max / used));

        Core.getBroadcastHandler().warAnnounce(this, "Un chunk à été capturé pour un total de " + resultat + " points.");
        return  resultat;
    }

    private void        launchPreparation() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.Preparation);
        this._timer = builder.delay(/*ConfigLoader.preparationPhase*/ 5, TimeUnit.SECONDS)
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
                .delay(ConfigLoader.rollbackPhase, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    private void        rollback() {
        for (Transaction<BlockSnapshot> t : _rollbackBlocks)
            t.getOriginal().restore(true, BlockChangeFlag.ALL);
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
            timeLeft = (long) ((ConfigLoader.preparationPhase) * 1000.0);
        else if (_state == WarState.War)
            timeLeft = (long) ((30.0 * 60.0) * 1000.0);
        else
            timeLeft = (long) (ConfigLoader.rollbackPhase * 1000.0);
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
            return ("War");
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
        player.sendMessage(Text.of(TextStyles.BOLD, "\n=== " + _attackerPoints + " - " + _defenderPoints + " ===", TextStyles.RESET));
    }

    private void        capture() {
        int             chunkMax = Core.getInfoCityMap().get(_cityDefender).getRank().getChunkMax();
        float           t = (chunkMax <= 15.0f ? 900.0f/chunkMax : 60f);

        this._vitesseCapture = (100.0f / (_attackers.size() * t));
    }

    private float       capture(Chunk chunk) {
        int             att = 0, def = 0, x, z;
        float           ret;
        Chunk           c;
        List<DBPlayer>  list = new ArrayList<>();

        list.addAll(_attackers);
        list.addAll(_defenders);


        for (DBPlayer player : list) {
            x = player.getLastChunkX();
            z = player.getLastChunkZ();

            c = Core.getChunkHandler().get(x, z);
            if (c != null && c == chunk) {
                if (isAttacker(player))
                    att++;
                else if (isDefender(player))
                    def++;
            }
        }

        ret = ((_vitesseCapture * att) - (0.45F * _vitesseCapture * def));
        return ret;
    }

    void         updateCapture() {
        List<Chunk>         updated = new ArrayList<>();
        float               v;

        for (DBPlayer att : _attackers) {
            Chunk   chunk = Core.getChunkHandler().get(att.getLastChunkX(), att.getLastChunkZ());

            if (chunk != null && !updated.contains(chunk) && !_capturedChunk.contains(chunk) && !chunk.isHomeblock() && !chunk.isOutpost()) {
                updated.add(chunk);
                if (_inCaptureChunk.containsKey(chunk)) {
                    v = _inCaptureChunk.get(chunk);
                    _inCaptureChunk.compute(chunk, (c, val) -> val - capture(chunk));
                }
                else {
                    v = 100;
                    _inCaptureChunk.put(chunk, 100 - capture(chunk));
                }
                Core.getBroadcastHandler().warAnnounce(this, "[Capture][" + (chunk.getPosX() * 16) + ";" + (chunk.getPosZ() * 16) + "] Temps restant avant capture " + Utils.toTime((int) (_inCaptureChunk.get(chunk) / (v - _inCaptureChunk.get(chunk)))) + " secondes.");
                if (_inCaptureChunk.get(chunk) <= 0) {
                    _capturedChunk.add(chunk);
                    _inCaptureChunk.remove(chunk);
                    addAttackerCapturePoints();
                }
            }
        }
    }
}
