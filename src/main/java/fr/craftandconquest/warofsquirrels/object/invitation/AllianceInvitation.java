package fr.craftandconquest.warofsquirrels.object.invitation;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AllianceInvitation extends Invitation {
    private final Faction factionSender;
    private final Permission permission;

    public AllianceInvitation(Player sender, Faction faction, Permission permission) {
        super(sender, InvitationType.Faction, faction);
        this.factionSender = sender.getCity().getFaction();
        this.permission = permission;
        WarOfSquirrels.instance.getBroadCastHandler().allianceInvitation(factionSender, faction);
    }

    @Override
    public void accept() {
        StringTextComponent message = new StringTextComponent(factionSender.getDisplayName() + " and " + factionReceiver.getDisplayName() + " are now allies.");
        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getDiplomacyHandler().CreateDiplomacy(factionSender, factionReceiver, true, permission);
        WarOfSquirrels.instance.getDiplomacyHandler().CreateDiplomacy(factionReceiver, factionSender, true, null);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
    }

    @Override
    public boolean      concern(Player player) {
        return (player.getCity().getFaction() == factionReceiver
                && (player.getCity().getOwner() == player
                || player.getAssistant()));
    }

    @Override
    public void refuse() {
        StringTextComponent toSender = new StringTextComponent(factionReceiver.getDisplayName() + " declined your invitation");
        StringTextComponent toReceiver = new StringTextComponent("The invitation from " + factionSender.getDisplayName() + " have been decline.");

        toSender.applyTextStyle(TextFormatting.RED);
        toReceiver.applyTextStyle(TextFormatting.RED);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionSender, null, toSender, true);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(factionReceiver, null, toReceiver, true);
    }
}
