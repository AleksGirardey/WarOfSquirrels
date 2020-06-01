package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.PartyWar;
import lombok.Getter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyWarInvitation extends Invitation {
    @Getter private final PartyWar partyWar;

    public PartyWarInvitation(Player receiver, Player sender, PartyWar partyWar) {
        super(receiver, sender, InvitationType.PartyWar);
        this.partyWar = partyWar;
        WarOfSquirrels.instance.getBroadCastHandler().partyInvitation(sender, receiver);
    }

    @Override
    public void accept() {
        partyWar.AddPlayer(receiver);
        WarOfSquirrels.instance.getBroadCastHandler()
                .partyChannel(partyWar, receiver.getDisplayName() + " has been added to the party");
    }

    @Override
    public void refuse() {
        TextComponent refuseTextParty = new StringTextComponent(receiver.getDisplayName() + " declined to join the party");
        TextComponent refuseTextPlayer = new StringTextComponent("The invitation from " + sender.getDisplayName() + " have been decline.");

        refuseTextParty.applyTextStyle(TextFormatting.RED);
        refuseTextPlayer.applyTextStyle(TextFormatting.RED);

        partyWar.Send(refuseTextParty);
        receiver.getPlayerEntity().sendMessage(refuseTextPlayer);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation invitation = (Invitation) obj;
        return (this.receiver.equals(invitation.getReceiver()) &&
                this.sender.equals(invitation.getSender()) &&
                this.invitationType.equals(invitation.getInvitationType()));
    }
}
