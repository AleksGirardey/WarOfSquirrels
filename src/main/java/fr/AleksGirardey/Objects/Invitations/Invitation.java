package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.scheduler.Task;

public abstract class Invitation {
    public enum Reason    {
        City,
        Alliance,
        PartyWar,
    }

    protected DBPlayer          _player;
    protected DBPlayer          _sender;
    protected City              _city;
    protected Task              _task;
    protected boolean           _executed = false;
    protected Reason            _reason;

    public Invitation(DBPlayer player, DBPlayer sender, Reason reason) {
        this._player = player;
        this._sender = sender;
        this._reason = reason;
    }

    public Invitation(DBPlayer sender, Reason reason, City city) {
        this._sender = sender;
        this._city = city;
        this._reason = reason;
    }

    public abstract void    accept();

    public abstract void    refuse();

    public DBPlayer         getPlayer() { return _player; }
    public boolean          isExecuted() { return _executed; }


    public boolean          concern(DBPlayer player) { return _player == player; }

    public Task             getTask() {
        return this._task;
    }

    public void             setTask(Task task) {
        this._task = task;
    }
}
