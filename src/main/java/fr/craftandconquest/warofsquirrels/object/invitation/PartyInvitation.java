package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import lombok.Getter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyInvitation extends Invitation {
    @Getter private final Party party;

    public PartyInvitation(Player receiver, Player sender, Party party) {
        super(receiver, sender, InvitationType.PartyWar);
        this.party = party;
        WarOfSquirrels.instance.getBroadCastHandler().partyInvitation(sender, receiver);
    }

    @Override
    public void accept() {
        party.AddPlayer(receiver);
        WarOfSquirrels.instance.getBroadCastHandler()
                .partyChannel(party, receiver.getDisplayName() + " a été ajouté au groupe.");
    }

    @Override
    public void refuse() {
        TextComponent refuseTextParty = new StringTextComponent(receiver.getDisplayName() + " a refusé de rejoindre le groupe.");
        TextComponent refuseTextPlayer = new StringTextComponent("L'invitation de " + sender.getDisplayName() + " a été refusé.");

        refuseTextParty.applyTextStyle(TextFormatting.RED);
        refuseTextPlayer.applyTextStyle(TextFormatting.RED);

        party.Send(refuseTextParty);
        receiver.getPlayerEntity().sendMessage(refuseTextPlayer);
    }

    @Override
    public void cancel() {
        TextComponent toSender = new StringTextComponent("L'invitation envoyé à '"
                + receiver.getDisplayName() + "' a expiré.");
        TextComponent toReceiver = new StringTextComponent("L'invitation de '"
                + sender.getDisplayName() + "' a expiré.");

        toSender.applyTextStyle(TextFormatting.RED);
        toReceiver.applyTextStyle(TextFormatting.RED);

        sender.getPlayerEntity().sendMessage(toSender);
        receiver.getPlayerEntity().sendMessage(toReceiver);
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
