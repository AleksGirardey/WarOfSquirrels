package fr.craftandconquest.warofsquirrels.listeners;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.carrier.Chest;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;

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

        if (Core.getShopHandler().get(loc.getBlockPosition()) != null) {
            Core.getShopHandler().get(loc.getBlockPosition()).actualize();
            return;
        } else if (Core.getLoanHandler().get(loc.getBlockPosition()) != null) {
            Core.getLoanHandler().get(loc.getBlockPosition()).actualize();
            return;
        }

        player = Core.getPlayerHandler().get(event.getCause().first(Player.class).orElse(null));
        if (player == null) return;

        if (Utils.checkShopFormat(datas)) {
            Optional<DirectionalData>       opt = loc.get(DirectionalData.class);

            if (!opt.isPresent())
                return;

            DirectionalData     direction = opt.get();
            if (direction.get(Keys.DIRECTION).get().equals(Direction.NORTH))
                chest = world.getLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + 1);
            else if (direction.get(Keys.DIRECTION).get().equals(Direction.SOUTH))
                chest = world.getLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() - 1);
            else if (direction.get(Keys.DIRECTION).get().equals(Direction.EAST))
                chest = world.getLocation(loc.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ());
            else if (direction.get(Keys.DIRECTION).get().equals(Direction.WEST))
                chest = world.getLocation(loc.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ());

            if (chest != null && chest.getTileEntity().orElse(null) != null)
                Core.getShopHandler().add(player, datas, sign, (Chest) chest.getTileEntity().orElse(null));
        } else if (Utils.checkLoanFormat(datas)) {
            Cubo cubo = Core.getCuboHandler().getFromName(datas.lines().get(1).toPlain());
            List<DBPlayer>  list = new ArrayList<>();

            if (Core.getLoanHandler().get(cubo) != null) {
                player.sendMessage(Text.of(TextColors.RED, "Ce cubo est déjà en location.", TextColors.RESET));
                return;
            }

            list.add(cubo.getOwner());
            list.add(cubo.getCity().getOwner());
            list.addAll(cubo.getCity().getAssistants());

            if (list.contains(player))
                Core.getLoanHandler().add(player, datas, sign);
            else
                player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas louer un cubo qui ne vous appartient pas."));
        }
    }

    @Listener (order = Order.FIRST)
    public void         onSignClick(InteractBlockEvent event, @First Player player) {
        BlockState      block = event.getTargetBlock().getState();

        if (block.getType() == BlockTypes.WALL_SIGN) {
            Vector3i        loc = event.getTargetBlock().getLocation().get().getBlockPosition();
            if (Core.getShopHandler().get(loc) != null) {
                if (event instanceof InteractBlockEvent.Primary)
                    Core.getShopHandler().get(loc).buy(player);
                else
                    Core.getShopHandler().get(loc).sell(player);
            } else if (Core.getLoanHandler().get(loc) != null) {
                if (event instanceof InteractBlockEvent.Primary)
                    Core.getLoanHandler().get(loc).leave(player);
                else
                    Core.getLoanHandler().get(loc).loan(player);
                Core.getLoanHandler().get(loc).actualize();
            }
        }
    }

    @Listener (order =  Order.FIRST)
    public void         onEntitySpawn(SpawnEntityEvent event) {
        event.getEntities().forEach(entity -> {
            if (types.contains(entity.getType()) &&
                    Core.getChunkHandler().get(entity.getLocation().getBlockX() / 16, entity.getLocation().getBlockZ() / 16, entity.getWorld()) != null) {
                Core.getLogger().warn("Cancel spawn of : " + entity.getType().getName());
                event.setCancelled(true);
            }
        });
    }
}
