package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.concurrent.TimeUnit;

public abstract class Invitation {
    protected Player            _player;
    protected Player            _sender;
    protected Task              _task;
    protected boolean           _executed = false;

    public abstract void accept();

    public abstract void refuse();

    public Player   getPlayer() { return _player; }
    public boolean  isExecuted() { return _executed; }

    public Invitation(Player player, Player _sender) {
        Scheduler       scheduler = Core.getPlugin().getScheduler();
        Task.Builder    builder = scheduler.createTaskBuilder();

        this._task = builder.execute(new Runnable() {
            public void run() {
                Core.getInvitationHandler().RefreshInvitations();
            }
        }).delay(30, TimeUnit.SECONDS).submit(Core.getMain());
        this._player = player;
        this._sender = _sender;
    }
}
