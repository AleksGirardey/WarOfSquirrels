package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.world.World;

public class OnPlayerRespawn {

    @Listener
    public void             onPlayerListener(RespawnPlayerEvent event) {
        DBPlayer            player = Core.getPlayerHandler().get(event.getTargetEntity());
        Transform<World>    transform;

        if (player.getCity() != null) {
            transform = new Transform<World>(Utils.getNearestSpawn(player));
            event.setToTransform(transform);
        }
    }
}
