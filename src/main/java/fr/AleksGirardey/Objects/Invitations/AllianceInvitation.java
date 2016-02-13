package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class AllianceInvitation extends Invitation {
    private int     _senderCityId;

    public AllianceInvitation(Player sender, int cityId) {
        super(sender, Reason.City, cityId);
        _senderCityId = Core.getPlayerHandler().<Integer>getElement(sender, "player_cityId");
        Core.getBroadcastHandler().allianceInvitationSend(_senderCityId, cityId);
    }

    @Override
    public void accept() {
        Core.getCityHandler().setDiplomacy(_senderCityId, _cityId, true);
        Core.Send(Core.getCityHandler().getElement(_senderCityId, "city_displayName")
                + " and "
                + Core.getCityHandler().<String>getElement(_cityId, "city_displayName")
                + " are now allies.");
    }

    @Override
    public void refuse() {
        _sender.sendMessage(
                Text.of(Core.getPlayerHandler().<String>getElement(_player, "player_displayName")
                        + " refuse your invitation"));
    }

    @Override
    public boolean  concern(Player player) {
        return (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") == _cityId
                && (Core.getPlayerHandler().isOwner(player)
                    || Core.getPlayerHandler().<Boolean>getElement(player, "player_assistant")));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation inv = (Invitation) obj;
        return (this._cityId == inv._cityId &&
                this._sender.equals(inv._sender) &&
                this._reason.equals(inv._reason));
    }
}
