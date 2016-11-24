package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class                OnPlayerSwitch {

    private static List<BlockType>  SwitchableBlocks;

    static {
        SwitchableBlocks = new ArrayList<BlockType>();
        SwitchableBlocks.add(BlockTypes.ACACIA_DOOR);
        SwitchableBlocks.add(BlockTypes.ACACIA_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.BIRCH_DOOR);
        SwitchableBlocks.add(BlockTypes.BIRCH_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.DARK_OAK_DOOR);
        SwitchableBlocks.add(BlockTypes.DARK_OAK_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.JUNGLE_DOOR);
        SwitchableBlocks.add(BlockTypes.JUNGLE_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.SPRUCE_DOOR);
        SwitchableBlocks.add(BlockTypes.SPRUCE_FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.WOODEN_DOOR);
        SwitchableBlocks.add(BlockTypes.FENCE_GATE);
        SwitchableBlocks.add(BlockTypes.STONE_BUTTON);
        SwitchableBlocks.add(BlockTypes.WOODEN_BUTTON);
        SwitchableBlocks.add(BlockTypes.LEVER);
    }

    @Listener
    public void             OnPlayerSwitch(InteractBlockEvent.Secondary event) {
        DBPlayer            player = Core.getPlayerHandler().get((Player) event.getCause().getNamedCauses().get("Source"));
        int                 x, z;
        Chunk               chunk;

        x = event.getTargetBlock().getLocation().get().getBlockX();
        z = event.getTargetBlock().getLocation().get().getBlockZ();
        if (Core.getChunkHandler().exists(x / 16, z / 16)) {
            if (SwitchableBlocks.contains(event.getTargetBlock().getState().getType()) &&
                    !Core.getPermissionHandler().ableTo(player,Core.getChunkHandler().get(x / 16, z / 16), "Switch", event.getTargetBlock().getPosition()))
                event.setCancelled(true);
        }
    }
}
