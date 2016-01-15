package fr.AleksGirardey.Objects.Invitations;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityInvitation extends Invitation
{
    public int          city;

    public CityInvitation(Player player, Player sender, int city) {
        super(player, sender);
        this.city = city;
        Core.getBroadcastHandler().cityInvitationSend(player, sender, city);
    }

    @Override
    public void accept() {
        Core.getCityHandler().newCitizen(_player, city);
        this._executed = true;
    }

    @Override
    public void refuse() {
        _sender.sendMessage(Text.of(_player.getDisplayNameData().displayName() + " refused your invitation."));
        this._executed = true;
    }
}
