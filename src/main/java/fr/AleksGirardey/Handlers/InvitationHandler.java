package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InvitationHandler {
    private List<Invitation>    invitationList = new ArrayList<>();

    public InvitationHandler() {}

    public void         createInvitation(Invitation invitation) {
        Scheduler scheduler = Core.getPlugin().getScheduler();
        Task.Builder    builder = scheduler.createTaskBuilder();

        Task            task = builder.execute(() -> {
            Core.getInvitationHandler().deleteTask(invitation);
        })
                .delay(15, TimeUnit.SECONDS)
                .submit(Core.getMain());

        invitation.setTask(task);
        invitationList.add(invitation);
    }

    private void         deleteTask(Invitation invitation) {
        //invitation.refuse();
        this.invitationList.remove(invitation);
    }

    public boolean      handleInvitation(DBPlayer player, boolean isAccepted) {
        Invitation inv = null;

        for (Invitation invitation : invitationList)
            if (invitation.concern(player))
                inv = invitation;

        if (inv != null) {
            if (isAccepted)
                inv.accept();
            else
                inv.refuse();
            inv.getTask().cancel();
            this.invitationList.remove(inv);
            return true;
        }
        return false;
    }

    //public void addInvitation(Invitation invitation) { invitationList.add(invitation); }

    public void RefreshInvitations() { invitationList.remove(0); }
}