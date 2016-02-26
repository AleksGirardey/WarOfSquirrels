package fr.AleksGirardey.Objects.Invitations;

import com.sun.org.apache.xpath.internal.operations.Equals;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public abstract class Invitation {
    public enum Reason    {
        City,
        Alliance,
        PartyWar,
    }

    protected Player            _player;
    protected Player            _sender;
    protected int               _cityId;
    protected Task              _task;
    protected boolean           _executed = false;
    protected Reason            _reason;

    public abstract void accept();

    public abstract void refuse();

    public Player   getPlayer() { return _player; }
    public boolean  isExecuted() { return _executed; }

    public Invitation(Player player, Player sender, Reason reason) {
        this._player = player;
        this._sender = sender;
        this._reason = reason;
    }

    public Invitation(Player sender, Reason reason, int cityId) {
        this._sender = sender;
        this._cityId = cityId;
        this._reason = reason;
    }

    public boolean      concern(Player player) { return _player == player; }

    public Task         getTask() {
        return this._task;
    }

    public void         setTask(Task task) {
        this._task = task;
    }
}
