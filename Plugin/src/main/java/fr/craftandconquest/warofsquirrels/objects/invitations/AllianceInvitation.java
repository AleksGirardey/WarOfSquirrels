package fr.craftandconquest.warofsquirrels.objects.invitations;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Permission;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class            AllianceInvitation extends Invitation {
    private Faction     _senderFaction;
    private Permission  _permission;

    public AllianceInvitation(DBPlayer sender, Faction faction, Permission perm) {
        super(sender, Reason.Faction, faction);
        _senderFaction = sender.getCity().getFaction();
        _permission = perm;
        Core.getBroadcastHandler().allianceInvitationSend(_senderFaction, faction);
    }

    @Override
    public void         accept() {
        Core.getDiplomacyHandler().add(_senderFaction, _faction, true, _permission);
        Core.getDiplomacyHandler().add(_faction, _senderFaction, true, null);
        Core.send(_senderFaction.getDisplayName()
                + " and "
                + _faction.getDisplayName()
                + " are now allies.");
    }

    @Override
    public void         refuse() {
        _sender.sendMessage(Text.of(TextColors.RED, _player.getDisplayName() + " refuse your invitation", TextColors.RESET));
        _player.sendMessage(Text.of(TextColors.RED, "The invitation from " + _sender.getDisplayName() + " have been refused.", TextColors.RESET));
    }

    @Override
    public boolean      concern(DBPlayer player) {
        return (player.getCity().getFaction() == _faction
                && (player.getCity().getOwner() == player
                    || player.isAssistant()));
    }

    @Override
    public boolean      equals(Object obj) {
        if (obj == null)
            return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation inv = (Invitation) obj;
        return (this._faction == inv._faction &&
                this._sender.equals(inv._sender) &&
                this._reason.equals(inv._reason));
    }
}
