package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public class InvitationHandler {

    private List<Invitation>    invitationList;

    public InvitationHandler() {

        this.invitationList = new ArrayList<Invitation>();
    }

    public boolean      acceptInvitation(Player player) {
        for (Invitation invitation : invitationList) {
            if (!invitation.isExecuted() && invitation.getPlayer().equals(player)) {
                invitation.accept();
                return true;
            }
        }
        return false;
    }

    public boolean      refuseInvitation(Player player) {
        for (Invitation invitation : invitationList) {
            if (!invitation.isExecuted() && invitation.getPlayer().equals(player)) {
                invitation.refuse();
                return true;
            }
        }
        return false;
    }

    public void addInvitation(Invitation invitation) { invitationList.add(invitation); }

    public void RefreshInvitations() { invitationList.remove(0); }
}