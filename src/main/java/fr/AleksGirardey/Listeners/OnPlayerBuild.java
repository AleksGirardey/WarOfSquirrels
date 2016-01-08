package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.function.Consumer;

public class OnPlayerBuild {

    @Listener
    public void onPlayerBuild(ChangeBlockEvent.Place event) {
        if (event.getCause().containsType(Player.class))
        {
            final Player  player = event.getCause().first(Player.class).orElseGet(null);
            int     x, z;
            Chunk   chunk;

            if (player == null) return;
            x = event.getTransactions().get(0).getOriginal().getLocation().get().getBlockX();
            z = event.getTransactions().get(0).getOriginal().getLocation().get().getBlockZ();
            chunk = new Chunk(x, z);
            try {
                if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ()))
                    if (!Core.getPermissionHandler().ableTo(player, Core.getChunkHandler().getId(chunk.getX(), chunk.getZ()), "Build"))
                        event.setCancelled(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

