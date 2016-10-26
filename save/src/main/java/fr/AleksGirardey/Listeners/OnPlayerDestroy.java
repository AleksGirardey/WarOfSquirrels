package fr.AleksGirardey.Listeners;

<<<<<<< HEAD
import fr.AleksGirardey.Objects.Cuboide.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
=======
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

import java.sql.SQLException;

public class OnPlayerDestroy {

<<<<<<< HEAD
    public static void CheckBuild(ChangeBlockEvent     event) {
        if (event.getCause().containsType(Player.class)) {



=======
    @Listener
    public void onPlayerDestroy(ChangeBlockEvent.Break event) {
        if (event.getCause().containsType(Player.class)) {
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            final Player  player = event.getCause().first(Player.class).orElseGet(null);
            int     x, z, chunkId;
            Chunk chunk;

<<<<<<< HEAD
            if (player == null){
                Core.Send("PLAYER NULL");
                return;
            }
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                x = transaction.getOriginal().getLocation().get().getBlockX();
                z = transaction.getOriginal().getLocation().get().getBlockZ();
                chunk = new Chunk(x, z);
                try {
                    if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())) {
                        chunkId = Core.getChunkHandler().getId(chunk.getX(), chunk.getZ());
                        if (!Core.getPermissionHandler().ableTo(
                                player,
                                chunkId,
                                "Build")) {
                            if (Core.getWarHandler().Contains(player)
                                    && Core.getWarHandler().ableTo(player, chunkId)) {
                                Core.getWarHandler().getWar(player).addRollbackBlock(
                                        event.getTransactions());
                            }
                            else {
                                event.setCancelled(true);
                                break;
                            }
                        } else {
                            if (Core.getWarHandler().ContainsDefender(Core.getChunkHandler().getCity(chunkId)))
                                Core.getWarHandler().getWar(Core.getChunkHandler().getCity(chunkId)).addRollbackBlock(
                                        event.getTransactions());
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Listener
    public void onPlayerDestroy(ChangeBlockEvent.Break event) {
        CheckBuild(event);
    }
=======
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
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
}
