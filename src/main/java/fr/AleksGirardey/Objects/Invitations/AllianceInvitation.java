package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class        AllianceInvitation extends Invitation {
    private City    _senderCity;

    public AllianceInvitation(DBPlayer sender, City city) {
        super(sender, Reason.City, city);
        _senderCity = sender.getCity();
        Core.getBroadcastHandler().allianceInvitationSend(_senderCity, city);
    }

    @Override
    public void         accept() {

        Core.getDiplomacyHandler().add(_senderCity, _city, true, null);
        Core.Send(_senderCity.getDisplayName()
                + " and "
                + _city.getDisplayName()
                + " are now allies.");
    }

    @Override
    public void         refuse() {
        _sender.sendMessage(Text.of(_player.getDisplayName() + " refuse your invitation"));
    }

    @Override
    public boolean      concern(DBPlayer player) {
        return (player.getCity() == _city
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
        return (this._city == inv._city &&
                this._sender.equals(inv._sender) &&
                this._reason.equals(inv._reason));
    }
}
