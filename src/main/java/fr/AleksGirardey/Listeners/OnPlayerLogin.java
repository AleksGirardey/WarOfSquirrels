package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Channels.CityChannel;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.sql.SQLException;

public class OnPlayerLogin {

    @Listener
    public void         onPlayerLogin(ClientConnectionEvent.Join event) throws SQLException {
        DBPlayer        player = Core.getPlayerHandler().get(event.getTargetEntity());

        if (player == null)
            Core.getPlayerHandler().add(event.getTargetEntity());

        player = Core.getPlayerHandler().get(event.getTargetEntity());

        if (player.getCity() != null) {
            InfoCity ic = Core.getInfoCityMap().get(player.getCity());
            if (ic.getChannel() == null)
                ic.setChannel(new CityChannel(player.getCity()));
            ic.getChannel().addMember(player.getUser().getPlayer().get());
            Core.getLogger().info("Player '" + player.getDisplayName() + "' added to city channel (" + player.getCity().getDisplayName() + ")");
        }
        Core.getBroadcastHandler().getGlobalChannel().addMember(player.getUser().getPlayer().get());
        player.getUser().getPlayer().get().setMessageChannel(Core.getBroadcastHandler().getGlobalChannel());
    }
}
