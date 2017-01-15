package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import org.spongepowered.api.scheduler.Task;

public abstract class Invitation {
    public enum Reason    {
        Faction,
        City,
        Alliance,
        PartyWar,
    }

    protected DBPlayer          _player;
    protected DBPlayer          _sender;
    protected Faction           _faction;
    protected Task              _task;
    protected boolean           _executed = false;
    protected Reason            _reason;

    public Invitation(DBPlayer player, DBPlayer sender, Reason reason) {
        this._player = player;
        this._sender = sender;
        this._reason = reason;
    }

    public Invitation(DBPlayer sender, Reason reason, Faction faction) {
        this._sender = sender;
        this._faction = faction;
        this._reason = reason;
    }

    public abstract void    accept();

    public abstract void    refuse();

    public DBPlayer         getPlayer() { return _player; }
    public DBPlayer         getSender() { return _sender; }

    public boolean          concern(DBPlayer player) { return _player == player; }

    public Task             getTask() {
        return this._task;
    }

    public void             setTask(Task task) {
        this._task = task;
    }
}
