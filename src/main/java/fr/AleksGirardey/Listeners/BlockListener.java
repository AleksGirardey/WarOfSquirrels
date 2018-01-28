package fr.AleksGirardey.Listeners;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Handlers.ShopHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Cubo;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BlockListener {

    private static List<BlockType>  ContainersBlock;
    private static List<BlockType>  SwitchableBlocks;
    private static List<EntityType> EntitiesBlocks;

    static {
        SwitchableBlocks = new ArrayList<>();
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
        SwitchableBlocks.add(BlockTypes.WALL_SIGN);

        ContainersBlock = new ArrayList<>();
        ContainersBlock.add(BlockTypes.CHEST);
        ContainersBlock.add(BlockTypes.ENDER_CHEST);
        ContainersBlock.add(BlockTypes.FURNACE);
        ContainersBlock.add(BlockTypes.ANVIL);
        ContainersBlock.add(BlockTypes.BREWING_STAND);
        ContainersBlock.add(BlockTypes.DISPENSER);
        ContainersBlock.add(BlockTypes.LIT_FURNACE);
        ContainersBlock.add(BlockTypes.HOPPER);

        EntitiesBlocks = new ArrayList<>();
        EntitiesBlocks.add(EntityTypes.ARMOR_STAND);
        EntitiesBlocks.add(EntityTypes.BOAT);
        EntitiesBlocks.add(EntityTypes.CHESTED_MINECART);
        EntitiesBlocks.add(EntityTypes.FURNACE_MINECART);
        EntitiesBlocks.add(EntityTypes.RIDEABLE_MINECART);
        EntitiesBlocks.add(EntityTypes.TNT_MINECART);
        EntitiesBlocks.add(EntityTypes.HOPPER_MINECART);
        EntitiesBlocks.add(EntityTypes.ITEM_FRAME);
        EntitiesBlocks.add(EntityTypes.PAINTING);
    }

    @Listener(order = Order.FIRST)
    public void         onEntitySpawn(SpawnEntityEvent event) {

    }

    @Listener(order = Order.FIRST)
    public void         onBlockBuild(ChangeBlockEvent event, @First Player player) {
        Text            message = Text.builder("Vous ne pouvez pas construire ici").color(TextColors.RED).build();
        final Boolean[] rollback = {false};
        boolean         destroy = (event instanceof ChangeBlockEvent.Break);

        if (destroy) {
            event.getTransactions().stream().filter(transaction -> transaction.getOriginal().getState().getType() == BlockTypes.WALL_SIGN)
                    .forEach(transaction -> {
                        if (Core.getShopHandler().get(transaction.getOriginal().getPosition()) != null
                                && !Core.getWarHandler().isConcerned(transaction.getOriginal().getPosition(), Core.getPlugin().getServer().getWorld(transaction.getOriginal().getWorldUniqueId()).get()))
                            Core.getShopHandler().delete(transaction.getOriginal().getPosition());
                    });
        }

        if (player != null && !Core.getPlayerHandler().get(player).hasAdminMode()) {
            if (!checkCuboPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            } else if (!checkChunkPerms(player, event)) {
                player.sendMessage(message);
                event.setCancelled(true);
            }
        }

        event.getTransactions().forEach(t -> {
            World world = Core.getPlugin().getServer().getWorld(t.getOriginal().getWorldUniqueId()).get();
            if (Core.getWarHandler().isConcerned(t.getOriginal().getPosition(), world))
                rollback[0] = true;
        });

        shouldRollback(rollback, event.getTransactions());
    }

    private void shouldRollback(Boolean[] rollback, List<Transaction<BlockSnapshot>> transactions) {
        if (rollback[0]) {
            transactions.forEach(t -> {
                World world = Core.getPlugin().getServer().getWorld(t.getOriginal().getWorldUniqueId()).get();
                Vector3i posO = t.getOriginal().getPosition();

                Core.getWarHandler().getWar(Core.getChunkHandler().get(
                        posO.getX() / 16,
                        posO.getZ() / 16,
                        world
                ).getCity()).addRollbackBlock(t);
            });
        }
    }

    @Listener(order = Order.FIRST)
    public void             onEntityInteract(InteractEntityEvent event) {
        Player              player = event.getCause().first(Player.class).orElseGet(null);
        DBPlayer            dbplayer = Core.getPlayerHandler().get(player);
        Location<World>     location = event.getTargetEntity().getLocation();

        int                 x, z;

        if (player != null && EntitiesBlocks.contains(event.getTargetEntity().getType())) {
            World               world = player.getWorld();
            x = location.getBlockX();
            z = location.getBlockZ();
            if (!dbplayer.hasAdminMode() && Core.getChunkHandler().exists(x / 16, z / 16, world)) {
                Chunk chunk = Core.getChunkHandler().get(x / 16, z / 16, world);
                if (event instanceof InteractEntityEvent.Primary && !checkEntityPerms(player, event))
                    event.setCancelled(true);
                else if (event instanceof InteractEntityEvent.Secondary &&
                        !Core.getPermissionHandler().ableTo(dbplayer, chunk, "Container", location.getBlockPosition()))
                    event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void             onBlockInteract(InteractBlockEvent.Secondary event, @First Player pl) {
        DBPlayer            player = Core.getPlayerHandler().get(pl);
        Location<World>     location = event.getTargetBlock().getLocation().orElse(null);
        World               world = player.getUser().getPlayer().get().getWorld();
        int                 x, z;

        ItemStack hand = pl.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
        if (location != null && player.getElapsedTimeClick()) {
            player.setLastClick(Instant.now().getEpochSecond());
            x = location.getBlockX();
            z = location.getBlockZ();

            if (hand != null && hand.getType().equals(ItemTypes.FEATHER)) {
                Chunk chunk = Core.getChunkHandler().get(x / 16, z / 16, world);
                Cubo cubo = Core.getCuboHandler().get(location.getBlockPosition());

                player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "===| Parcelle [" + (x / 16) + ";" + (z / 16) + "] |===", TextColors.RESET));
                player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Propriétaire : " + (chunk == null ? "Nature" : chunk.getCity().getDisplayName()), TextColors.RESET));
                if (cubo != null)
                    player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "===| Cubo '" + cubo.getName() + "' [" + cubo.getOwner().getDisplayName() + "] |===", TextColors.RESET));
            }


            if (!player.hasAdminMode() && Core.getChunkHandler().exists(x / 16, z / 16, world) && !Core.getWarHandler().isConcerned(location.getBlockPosition(), world)) {
                if (SwitchableBlocks.contains(event.getTargetBlock().getState().getType()) &&
                        !Core.getPermissionHandler().ableTo(player, Core.getChunkHandler().get(x / 16, z / 16, world), "Switch", event.getTargetBlock().getPosition()))
                    event.setCancelled(true);
                else if (ContainersBlock.contains(event.getTargetBlock().getState().getType()) &&
                        !Core.getPermissionHandler().ableTo(player, Core.getChunkHandler().get(x / 16, z / 16, world), "Container", event.getTargetBlock().getPosition()))
                    event.setCancelled(true);
            }
        }
    }

    @Listener(order = Order.FIRST)
    public void             onBlockExplosion(ExplosionEvent event) {
        ((Cancellable) event).setCancelled(true);
    }

    private boolean         checkCubo(DBPlayer player, Vector3i pos) {
        Cubo cubo = Core.getCuboHandler().get(pos);

        if (cubo != null) {
            if (!cubo.getInList().contains(player)) {
                return Core.getWarHandler().Contains(player) && Core.getWarHandler().ableTo(player, cubo);
            }
        }
        return true;
    }

    private boolean         checkCuboPerms(Player player, ChangeBlockEvent event) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!checkCubo(dbPlayer, transaction.getOriginal().getPosition()))
                return false;
        }
        return true;
    }

    private boolean         checkEntityPerms(Player player, InteractEntityEvent event) {
        DBPlayer dbPlayer = Core.getPlayerHandler().get(player);

        return checkPerm(dbPlayer, event.getTargetEntity().getLocation().getBlockPosition(), player.getWorld());
    }

    private boolean         checkChunkPerms(Player player, ChangeBlockEvent event) {
        DBPlayer dbPlayer = Core.getPlayerHandler().get(player);

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!checkPerm(dbPlayer, transaction.getOriginal().getLocation().get().getBlockPosition(),
                    player.getWorld())) {
                return false;
            }
        }
        return true;
    }

    private boolean         checkPerm(DBPlayer dbPlayer, Vector3i pos, World world) {
        Chunk chunk = Core.getChunkHandler().get(pos.getX() / 16, pos.getZ() / 16, world);

        if (chunk != null) {
            if (!Core.getPermissionHandler().ableTo(dbPlayer, chunk, Permissions.BUILD, pos)) {
                return Core.getWarHandler().Contains(dbPlayer) && Core.getWarHandler().ableTo(dbPlayer, chunk);
            }
        }
        return true;
    }
}