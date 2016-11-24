package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityInvitation extends Invitation {

    public CityInvitation(DBPlayer player, DBPlayer sender, City city) {
        super(player, sender, Reason.City);
        this._city = city;
        Core.getBroadcastHandler().cityInvitationSend(player, sender, city);
    }

    @Override
    public void accept() {
        Core.getInfoCityMap().get(_city).getChannel().addMember(_player.getUser().getPlayer().get());
        Core.getCityHandler().newCitizen(_player, _city);
    }

    @Override
    public void refuse() {
        _sender.sendMessage(Text.of(_player.getDisplayName() + " refuse your invitation"));
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
