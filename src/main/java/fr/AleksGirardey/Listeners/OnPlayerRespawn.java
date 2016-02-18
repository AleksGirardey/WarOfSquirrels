package fr.AleksGirardey.Listeners;

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

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            transform = new Transform<World>(Utils.getNearestSpawn(player));
            event.setToTransform(transform);
        }
    }
}
