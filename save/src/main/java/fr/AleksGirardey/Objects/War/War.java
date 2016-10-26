package fr.AleksGirardey.Objects.War;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import fr.AleksGirardey.Objects.War.WarTask;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.BlockChangeFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class War {

    public enum WarState{
        Declaration,
        Preparation,
        War,
        Rollback
    }

    String          _tag;
    String          _attackerName;
    String          _defenderName;
    int             _cityAttacker;
    int             _cityDefender;
    List<Player>    _attackers;
    List<Player>    _defenders;
    Task            _timer;
    WarState        _state;
    int             _attackersLimit;
    long            _timeStart;
    int             _attackerPoints = 0;
    int             _defenderPoints = 0;

    List<Transaction<BlockSnapshot>> _rollbackBlocks;

    public War(int attacker, int defender, List<Player> attackersList) {
        this._cityAttacker = attacker;
        this._cityDefender = defender;
        _attackers = new ArrayList<>(attackersList);
        _defenders = new ArrayList<>(Core.getCityHandler().getOnlinePlayers(defender));
        _rollbackBlocks = new ArrayList<>();
        _attackersLimit = _defenders.size() + 1;
        _state = WarState.Preparation;
        _tag = setTag(attacker, defender);
        Core.Send("A war is about to start ! " + _attackerName + " attack " + _defenderName + " !");
        launchPreparation();
    }

    private String setTag(int att, int def) {
        _attackerName = Core.getCityHandler().<String>getElement(att, "city_displayName");
        _defenderName = Core.getCityHandler().<String>getElement(def, "city_displayName");
        return (_attackerName.substring(0, 3) + _defenderName.substring(0, 3));
    }

    public boolean      addAttacker(Player player) {
        if (_attackers.size() == _attackersLimit) {
            player.sendMessage(Text.of("You can't join this war, wait for defenders to join"));
            return false;
        }
        _attackers.add(player);
        Core.getBroadcastHandler().warAnnounce(this, Core.getPlayerHandler().<String>getElement(player, "player_displayName") +
                " join as Attacker ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public boolean      addDefender(Player player) {
        _defenders.add(player);
        ++_attackersLimit;
        Core.getBroadcastHandler().warAnnounce(this, Core.getPlayerHandler().<String>getElement(player, "player_displayName") +
                " join as Defender ! [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public void         addRollbackBlock(List<Transaction<BlockSnapshot>> transaction) {
        int size = _rollbackBlocks.size();
        for (Transaction<BlockSnapshot> t : transaction)
            _rollbackBlocks.add(t);
        Core.Send("Size from '" + size + "' to '" + _rollbackBlocks.size() + "'");
    }

    public void         addDefenderPoints(int points) {
        this._defenderPoints += points;
    }

    public void         addDefenderPoints() {
        this._defenderPoints += 166 / (5 * _attackers.size());
    }

    public void         addAttackerPoints() {
        this._attackerPoints += 166 / (5 * _defenders.size());
    }

    public void         addAttackerPointsTarget() {
        this._attackerPoints += 650;
    }

    private void        launchPreparation() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.Preparation);
        this._timer = builder.delay(2, TimeUnit.SECONDS)
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
        this._timer = builder.execute(task)
                .interval(1, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    public void         lauchRollback() {
        Task.Builder    builder = Core.getPlugin().getScheduler().createTaskBuilder();

        _timeStart = System.currentTimeMillis();
        Core.getBroadcastHandler().warAnnounce(this, WarState.Rollback);
        this._timer = builder.execute(() -> {
            this.rollback();
            Core.getWarHandler().delete(this);
        })
                .delay(1, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    private void        rollback() {
        for (Transaction<BlockSnapshot> t : _rollbackBlocks)
            t.getOriginal().restore(true, BlockChangeFlag.ALL);
    }

    public boolean      checkWin() {
        if (_defenderPoints >= 1000)
            Core.Send(_attackerName + " fail to win his attack against " + _defenderName + " (" + _attackerPoints + " to " + _defenderPoints + ")");
        else if (_attackerPoints >= 1000)
            Core.Send(_attackerName + " win his attack against " + _defenderName + " (" + _attackerPoints + " to " + _defenderPoints + ")");
        else
            return false;
        this._state = WarState.Rollback;
        return true;
    }

    public boolean      contains(Player player) {
        return (_attackers.contains(player) || _defenders.contains(player));
    }

    public boolean      contains(int cityId) {
        return (_cityAttacker == cityId || _cityDefender == cityId);
    }

    public String       timeLeft() {
        long            time = System.currentTimeMillis();
        long            timeLeft, delta;
        double          elapsedSeconds;
        int             minutes, seconds;

        String          res = "";

        delta = time - _timeStart;
        if (_state == WarState.Preparation)
            timeLeft = (long) ((2.0 * 60.0) * 1000.0);
        else if (_state == WarState.War)
            timeLeft = (long) ((30.0 * 60.0) * 1000.0);
        else
            timeLeft = (long) (60 * 1000.0);
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

    public List<Player>     getProtagonists() {
        List<Player>        list = new ArrayList<>(_defenders);

        for (Player p : _attackers)
            list.add(p);
        return list;
    }

    public boolean          removePlayer(Player player) {
        int                 cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        if (_attackers.contains(player))
            _attackers.remove(player);
        else if (_defenders.contains(player) && cityId != _cityDefender) {
            _defenders.remove(player);
            --_attackersLimit;
        }
        else
            return false;
        Core.getBroadcastHandler().warAnnounce(this, Core.getPlayerHandler().<String>getElement(player, "player_displayName") +
                " left the war [" + _attackers.size() + "/" + _attackersLimit + "]");
        return true;
    }

    public boolean          isAttacker(Player player) { return _attackers.contains(player); }
    public boolean          isDefender(Player player) { return _defenders.contains(player); }
    public String           getAttackerName() { return _attackerName; }
    public String           getDefenderName() { return _defenderName; }
    public int              getAttacker() { return _cityAttacker; }
    public int              getDefender() { return _cityDefender; }
    public int              getAttackerPoints() { return _attackerPoints; }
    public int              getDefenderPoints() { return _defenderPoints; }

    public void     displayInfo(Player player) {
        player.sendMessage(Text.of("===| " + this._tag + " |==="));
        player.sendMessage(Text.of("Attackers [" + _attackerName + "] : " + Utils.getStringFromPlayerList(_attackers)));
        player.sendMessage(Text.of("Defenders [" + _defenderName + "] : " + Utils.getStringFromPlayerList(_defenders)));
        player.sendMessage(Text.of("=== " + _attackerPoints + " - " + _defenderPoints + " ==="));
        player.sendMessage(Text.of("Phase : " + this.getPhase() + " (time left : " + timeLeft() + ")"));
    }
}