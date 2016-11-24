package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import java.sql.SQLException;

public class            OnPlayerDestroy {

    public static void  CheckBuild(ChangeBlockEvent event) {
        if (event.getCause().containsType(Player.class)) {
            final Player    player = event.getCause().first(Player.class).orElseGet(null);
            DBPlayer        dbPlayer = Core.getPlayerHandler().get(player);
            int             x, z;
            Chunk chunk;

            if (player == null){
                Core.Send("PLAYER NULL");
                return;
            }
            for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
                x = transaction.getOriginal().getLocation().get().getBlockX();
                z = transaction.getOriginal().getLocation().get().getBlockZ();
                chunk = Core.getChunkHandler().get(x / 16, z / 16);
                if (chunk != null) {
                    if (!Core.getPermissionHandler().ableTo(
                            dbPlayer,
                            chunk,
                            "Build",
                            transaction.getOriginal().getPosition())) {
                        if (Core.getWarHandler().Contains(dbPlayer)
                                && Core.getWarHandler().ableTo(dbPlayer, chunk)) {
                            Core.getWarHandler().getWar(dbPlayer).addRollbackBlock(
                                    event.getTransactions());
                        }
                        else {
                            event.setCancelled(true);
                            break;
                        }
                    } else {
                        if (Core.getWarHandler().ContainsDefender(chunk.getCity()))
                            Core.getWarHandler().getWar(chunk.getCity()).addRollbackBlock(
                                    event.getTransactions());
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerDestroy(ChangeBlockEvent.Break event) {
        CheckBuild(event);
    }
}
