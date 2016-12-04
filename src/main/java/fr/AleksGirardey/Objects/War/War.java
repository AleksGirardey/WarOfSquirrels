package fr.AleksGirardey.Objects.War;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
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

    private String          _tag;
    private City            _cityAttacker;
    private City            _cityDefender;
    private List<DBPlayer>  _attackers;
    private List<DBPlayer>  _defenders;
    private Task            _timer;
    private WarState        _state;
    private int             _attackersLimit;
    private long            _timeStart;
    private int             _attackerPoints = 0;
    private int             _defenderPoints = 0;

    private List<Transaction<BlockSnapshot>>    _rollbackBlocks;

    public          War(City attacker, City defender, List<DBPlayer> attackersList) {
        _cityAttacker = attacker;
        _cityDefender = defender;
        _attackers = new ArrayList<>(attackersList);
        _defenders = new ArrayList<>(Core.getCityHandler().getOnlineDBPlayers(defender));
        _rollbackBlocks = new ArrayList<>();
        _attackersLimit = _defenders.size() + 1;
        _state = WarState.Preparation;
        _tag = setTag();
        Core.Send("A war is about to start ! " + _cityAttacker.getDisplayName()
                + " attack " + _cityDefender.getDisplayName() + " !");
        launchPreparation();
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

    // SAVE ON DB OR FILE !!!
    public void         addRollbackBlock(Transaction<BlockSnapshot> transaction) {
        int size = _rollbackBlocks.size();

        _rollbackBlocks.add(transaction);
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

    public boolean          isAttacker(DBPlayer player) { return _attackers.contains(player); }
    public boolean          isDefender(DBPlayer player) { return _defenders.contains(player); }
    public City             getAttacker() { return _cityAttacker; }
    public City             getDefender() { return _cityDefender; }
    public int              getAttackerPoints() { return _attackerPoints; }
    public int              getDefenderPoints() { return _defenderPoints; }

    public void     displayInfo(DBPlayer player) {
        player.sendMessage(Text.of("===| " + this._tag + " |==="));
        player.sendMessage(Text.of("Attackers [" + _cityAttacker.getDisplayName() + "] : " + Utils.getStringFromPlayerList(_attackers)));
        player.sendMessage(Text.of("Defenders [" + _cityDefender.getDisplayName() + "] : " + Utils.getStringFromPlayerList(_defenders)));
        player.sendMessage(Text.of("=== " + _attackerPoints + " - " + _defenderPoints + " ==="));
        player.sendMessage(Text.of("Phase : " + this.getPhase() + " (time left : " + timeLeft() + ")"));
    }
}
