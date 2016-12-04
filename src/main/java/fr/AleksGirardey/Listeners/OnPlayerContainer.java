package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import java.util.ArrayList;
import java.util.List;

public class                OnPlayerContainer {

    private static List<BlockType>  ContainersBlock;

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
    public void         onPlayerContainer(InteractBlockEvent.Secondary event) {
        DBPlayer        player = Core.getPlayerHandler().get((Player) event.getCause().all().get(0));
        int             x, z;
        Chunk           chunk;

        x = event.getTargetBlock().getLocation().get().getBlockX();
        z = event.getTargetBlock().getLocation().get().getBlockZ();
        chunk = Core.getChunkHandler().get(x / 16, z / 16);

        if (chunk != null)
            if(ContainersBlock.contains(event.getTargetBlock().getState().getType())
                    && !Core.getPermissionHandler().ableTo(
                    player,
                    chunk,
                    "Container", event.getTargetBlock().getPosition()))
                event.setCancelled(true);
    }
}