package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Handlers.PlayerHandler;
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
    }
}
