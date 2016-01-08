package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import java.sql.SQLException;

public class OnPlayerContainer {

    @Listener
    public void onPlayerContainer(InteractBlockEvent.Secondary event) {
        Player  player = (Player) event.getCause().all().get(0);
        int     x, z;
        Chunk chunk;

        x = event.getTargetBlock().getLocation().get().getBlockX();
        z = event.getTargetBlock().getLocation().get().getBlockZ();
        chunk = new Chunk(x, z);

        try {
            if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ()))
                if(!Core.getPermissionHandler().ableTo(
                        player,
                        Core.getChunkHandler().getId(chunk.getX(), chunk.getZ()),
                        "Container"))
                    event.setCancelled(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}