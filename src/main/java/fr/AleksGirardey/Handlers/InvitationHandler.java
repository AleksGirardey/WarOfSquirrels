package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InvitationHandler {
    private List<Invitation>    invitationList;

    public InvitationHandler() {

        this.invitationList = new ArrayList<Invitation>();
    }

    public void         createInvitation(final Invitation invitation) {
        Scheduler scheduler = Core.getPlugin().getScheduler();
        Task.Builder    builder = scheduler.createTaskBuilder();

        invitation.setTask(
                builder.execute(new Runnable() {
                    public void run() {
                        Core.getInvitationHandler().deleteTask(invitation); }})
                        .delay(30, TimeUnit.SECONDS)
                        .submit(Core.getMain()));
        invitationList.add(invitation);
    }

    public void         deleteTask(Invitation invitation) {
        invitation.refuse();
        this.invitationList.remove(invitation);
    }

    public boolean      handleInvitation(Player player, boolean isAccepted) {
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

    public void addInvitation(Invitation invitation) { invitationList.add(invitation); }

    public void RefreshInvitations() { invitationList.remove(0); }
}