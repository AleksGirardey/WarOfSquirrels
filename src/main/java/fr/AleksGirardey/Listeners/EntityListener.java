package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityListener {
    private static List<EntityType>        types;

    static {
        types = new ArrayList<>();
        types.add(EntityTypes.BAT);
        types.add(EntityTypes.CAVE_SPIDER);
        types.add(EntityTypes.CREEPER);
        types.add(EntityTypes.ENDERMITE);
        types.add(EntityTypes.GHAST);
        types.add(EntityTypes.GIANT);
        types.add(EntityTypes.GUARDIAN);
        types.add(EntityTypes.MAGMA_CUBE);
        types.add(EntityTypes.PIG_ZOMBIE);
        types.add(EntityTypes.POLAR_BEAR);
        types.add(EntityTypes.SHULKER);
        types.add(EntityTypes.SILVERFISH);
        types.add(EntityTypes.SKELETON);
        types.add(EntityTypes.SPIDER);
        types.add(EntityTypes.WITCH);
        types.add(EntityTypes.ZOMBIE);
    }

    @Listener (order = Order.FIRST)
    public void             onSignPlaced(ChangeSignEvent event) {
        World               world = event.getTargetTile().getWorld();
        Location<World>     loc = event.getTargetTile().getLocation(), chest = null;
        Sign                sign = event.getTargetTile();
        SignData            datas = event.getText();
        DBPlayer            player;

        if (!datas.lines().get(0).equals(Text.of("[Shop]"))) return;

        player = Core.getPlayerHandler().get(event.getCause().first(Player.class).orElse(null));
        if (player == null) return;

        /* Chest doit être placé derrière le panneau, on vérifie les 4 blocs possible [x + 1][x - 1][z + 1][z - 1] */
        if (world.getBlock(loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ()).getType() == BlockTypes.CHEST)
            chest = world.getLocation(loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ());
        else if (world.getBlock(loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()).getType() == BlockTypes.CHEST)
            chest = world.getLocation(loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ());
        else if (world.getBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1).getType() == BlockTypes.CHEST)
            chest = world.getLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1);
        else if (world.getBlock(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1).getType() == BlockTypes.CHEST)
            chest = world.getLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1);

        if (chest != null)
            Core.getShopHandler().add(player, datas, sign, (Chest) chest.getTileEntity().orElse(null));
    }

    @Listener (order =  Order.FIRST)
    public void         onEntitySpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if (types.contains(entity.getType()) &&
                    Core.getCuboHandler().get(entity.getLocation().getBlockPosition()) != null)
                event.setCancelled(true);
        });
    }
}
