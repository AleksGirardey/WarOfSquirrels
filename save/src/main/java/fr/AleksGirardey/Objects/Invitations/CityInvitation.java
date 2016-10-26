package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityInvitation extends Invitation {

    public CityInvitation(Player player, Player sender, int cityId) {
        super(player, sender, Reason.City);
        this._cityId = cityId;
        Core.getBroadcastHandler().cityInvitationSend(player, sender, cityId);
    }

    @Override
    public void accept() {
<<<<<<< HEAD
        Core.getInfoCityMap().get(_cityId).getChannel().addMember(_player);
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        Core.getCityHandler().newCitizen(_player, _cityId);
    }

    @Override
    public void refuse() {
        _sender.sendMessage(
                Text.of(Core.getPlayerHandler().<String>getElement(_player, "player_displayName")
                + " refuse your invitation"));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!Invitation.class.isAssignableFrom(obj.getClass()))
            return false;
        final Invitation inv = (Invitation) obj;
        return (this._player.equals(inv._player) &&
                this._sender.equals(inv._sender) &&
                this._reason.equals(inv._reason));
    }
}
