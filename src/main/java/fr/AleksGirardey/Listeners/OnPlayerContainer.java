package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OnPlayerContainer {

    static List<BlockType> ContainersBlock;

    static {
        ContainersBlock = new ArrayList<BlockType>();
        ContainersBlock.add(BlockTypes.CHEST);
        ContainersBlock.add(BlockTypes.ENDER_CHEST);
        ContainersBlock.add(BlockTypes.FURNACE);
        ContainersBlock.add(BlockTypes.ANVIL);
        ContainersBlock.add(BlockTypes.BREWING_STAND);
        ContainersBlock.add(BlockTypes.DISPENSER);
        ContainersBlock.add(BlockTypes.LIT_FURNACE);
        ContainersBlock.add(BlockTypes.HOPPER);
    }

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
                if(ContainersBlock.contains(event.getTargetBlock().getState().getType())
                        && !Core.getPermissionHandler().ableTo(
                            player,
                            Core.getChunkHandler().getId(chunk.getX(), chunk.getZ()),
                            "Container"))
                    event.setCancelled(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}