package fr.craftandconquest.warofsquirrels.objects.invitations;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CityInvitation extends Invitation {
    private City _city;

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
        _sender.sendMessage(Text.of(TextColors.RED, _player.getDisplayName() + " refuse your invitation", TextColors.RESET));
        _player.sendMessage(Text.of(TextColors.RED, "The invitation from " + _sender.getDisplayName() + " have been refused.", TextColors.RESET));
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
