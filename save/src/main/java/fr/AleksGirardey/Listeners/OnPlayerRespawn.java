package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.Utilitaires.Utils;
=======
import fr.AleksGirardey.Objects.Utils;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.world.World;

<<<<<<< HEAD
=======
import java.sql.SQLException;

>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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
