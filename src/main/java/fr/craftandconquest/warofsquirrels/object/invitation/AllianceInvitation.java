package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class AllianceInvitation extends Invitation {
    private final Faction factionSender;
    private final Permission permission;

    public AllianceInvitation(FullPlayer sender, Faction faction, Permission permission) {
        super(sender, InvitationType.Alliance, faction);
        this.factionSender = sender.getCity().getFaction();
        this.permission = permission;
        WarOfSquirrels.instance.getBroadCastHandler().allianceInvitation(factionSender, faction);
    }

    @Override
    public void accept() {
        MutableComponent message = ChatText.Colored(factionSender.getDisplayName() + " et " + factionReceiver.getDisplayName() + " sont désormais alliés.", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getDiplomacyHandler().CreateDiplomacy(factionSender, factionReceiver, true, permission);
        WarOfSquirrels.instance.getDiplomacyHandler().CreateDiplomacy(factionReceiver, factionSender, true, null);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
    }

    @Override
    public boolean concern(FullPlayer player) {
        return (player.getCity().getFaction() == factionReceiver
                && (player.getCity().getOwner() == player
                || player.getAssistant()));
    }

    @Override
    public void refuse() {
        MutableComponent toSender = ChatText.Error(factionReceiver.getDisplayName() + " a décliné votre invitation");
        MutableComponent toReceiver = ChatText.Error("L'invitation de " + factionSender.getDisplayName() + " a été décliné.");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionSender, null, toSender, true);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionReceiver, null, toReceiver, true);
    }

    @Override
    public void cancel() {
        MutableComponent toSender = ChatText.Error("L'invitation envoyé à '"
                + factionReceiver.getDisplayName() + "' a expiré.");
        MutableComponent toReceiver = ChatText.Error("L'invitation de '"
                + factionSender.getDisplayName() + "' a expiré");

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionSender, null, toSender, true);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionReceiver, null, toReceiver, true);
    }
}
