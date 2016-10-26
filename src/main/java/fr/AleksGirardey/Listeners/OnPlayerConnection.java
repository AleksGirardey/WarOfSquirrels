package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Channels.CityChannel;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Messages.AdminMessage;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class OnPlayerConnection {

    private AdminMessage        adminChannel = new AdminMessage();

    @Listener
    public void     onPlayerConnection(ClientConnectionEvent.Join event) {
        Player      player = event.getTargetEntity();

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            int         cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
            InfoCity    ic = Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));
            if (ic.getChannel() == null)
                ic.setChannel(new CityChannel(cityId));
            ic.getChannel().addMember(player);
            Core.getLogger().info("Player '" + Core.getPlayerHandler().<String>getElement(player, "player_displayName") + "' added to city channel (" + cityId + ")");
        }
        Core.getBroadcastHandler().getGlobalChannel().addMember(player);
        player.setMessageChannel(Core.getBroadcastHandler().getGlobalChannel());
    }
}
