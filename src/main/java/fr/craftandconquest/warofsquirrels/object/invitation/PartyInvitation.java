package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;

public class PartyInvitation extends Invitation {
    @Getter
    private final Party party;

    public PartyInvitation(FullPlayer receiver, FullPlayer sender, Party party) {
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
        MutableComponent refuseTextParty = ChatText.Error(receiver.getDisplayName() + " a refusé de rejoindre le groupe.");
        MutableComponent refuseTextPlayer = ChatText.Error("L'invitation de " + sender.getDisplayName() + " a été refusé.");

        party.Send(refuseTextParty);
        receiver.sendMessage(refuseTextPlayer);
    }

    @Override
    public void cancel() {
        MutableComponent toSender = ChatText.Error("L'invitation envoyé à '"
                + receiver.getDisplayName() + "' a expiré.");
        MutableComponent toReceiver = ChatText.Error("L'invitation de '"
                + sender.getDisplayName() + "' a expiré.");

        sender.sendMessage(toSender);
        receiver.sendMessage(toReceiver);
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
