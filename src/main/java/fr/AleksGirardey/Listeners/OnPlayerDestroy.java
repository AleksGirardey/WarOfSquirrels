package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class OnPlayerDestroy {

    @Listener
    public void onPlayerDestroy(ChangeBlockEvent.Break event) {
        if (event.getCause().containsType(Player.class)) {
            final Player  player = event.getCause().first(Player.class).orElseGet(null);
            int     x, z, chunkId;
            Chunk chunk;

            if (player == null) return;
            x = event.getTransactions().get(0).getOriginal().getLocation().get().getBlockX();
            z = event.getTransactions().get(0).getOriginal().getLocation().get().getBlockZ();
            chunk = new Chunk(x, z);
            try {
                if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())) {
                    chunkId = Core.getChunkHandler().getId(chunk.getX(), chunk.getZ());
                    if (!Core.getPermissionHandler().ableTo(
                            player,
                            chunkId,
                            "Build")) {
                        if (Core.getWarHandler().Contains(player)
                                && Core.getWarHandler().ableTo(player, chunkId))
                            Core.getWarHandler().getWar(player).addRollbackBlock(
                                    event.getTransactions().get(0).getOriginal());
                        else
                            event.setCancelled(true);

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
