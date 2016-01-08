package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.world.World;

import java.sql.SQLException;

public class OnPlayerRespawn {

    @Listener
    public void     onPlayerListener(RespawnPlayerEvent event) {
        Player              player = event.getTargetEntity();
        Transform<World>    transform;

        try {
            if (Core.getPlayerHandler().getCity(player) != 0) {
                transform = new Transform<World>(Utils.getNearestSpawn(player));
                event.setToTransform(transform);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
