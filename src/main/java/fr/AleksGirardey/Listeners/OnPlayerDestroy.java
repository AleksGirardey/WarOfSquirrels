package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.sql.SQLException;

public class OnPlayerDestroy {

    public static void CheckBuild(ChangeBlockEvent     event) {
        if (event.getCause().containsType(Player.class)) {



            final Player  player = event.getCause().first(Player.class).orElseGet(null);
            int     x, z, chunkId;
            Chunk chunk;

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
}
