package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.sql.SQLException;
import java.util.Optional;
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
                if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())) {
                    int chunkId = Core.getChunkHandler().getId(chunk.getX(), chunk.getZ());
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

