package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.sql.SQLException;

/**
 * Created by aleks on 26/10/15.
 */
public class OnPlayerLogin {

    @Listener
    public void onPlayerLogin(ClientConnectionEvent.Join event) throws SQLException {
        Player          player = event.getTargetEntity();
        PlayerHandler   playerHandler = Core.getPlayerHandler();

        if (!playerHandler.exists(player))
            playerHandler.add(player);
    }
}
