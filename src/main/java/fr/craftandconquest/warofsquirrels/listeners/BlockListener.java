package fr.craftandconquest.warofsquirrels.listeners;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.world.ExplosionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @Listener
    public void         onBlockDestroy(ChangeBlockEvent.Break event, @First Player player) {
        event.getTransactions().stream().filter(transaction -> transaction.getOriginal().getState().getType() == BlockTypes.WALL_SIGN)
                .forEach(transaction -> {
                    if (Core.getShopHandler().get(transaction.getOriginal().getPosition()) != null
                            && !Core.getWarHandler().isConcerned(transaction.getOriginal().getPosition(), Core.getPlugin().getServer().getWorld(transaction.getOriginal().getWorldUniqueId()).get()))
                        Core.getShopHandler().delete(transaction.getOriginal().getPosition());
                });

        handleEventConstruction(event, player);
    }

    @Listener
    public void         onBlockPlaced(ChangeBlockEvent.Place event, @First Player player) {
        handleEventConstruction(event, player);
    }

    private void         handleEventConstruction(ChangeBlockEvent event, Player player) {
        Text            message = Text.builder("Vous ne pouvez pas construire ici").color(TextColors.RED).build();
        final Boolean[] rollback = {false};

        if (player != null && !Core.getPlayerHandler().get(player).hasAdminMode()) {
            if (!checkCuboPerms(player, event, Permissions.BUILD)
                    || !checkChunkPerms(player, event, Permissions.BUILD)) {
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
    public void             onEntityInteract(InteractEntityEvent event, @First Player player) {
        DBPlayer            dbplayer = Core.getPlayerHandler().get(player);
        Location<World>     location = event.getTargetEntity().getLocation();
        Text                message = Text.of(TextColors.RED, "Vous ne pouvez pas faire ça.", TextColors.RESET);

        int                 x, z;

        if (player != null && EntitiesBlocks.contains(event.getTargetEntity().getType())) {
            World               world = player.getWorld();
            x = location.getBlockX();
            z = location.getBlockZ();
            if (!dbplayer.hasAdminMode() && Core.getChunkHandler().exists(x / 16, z / 16, world)) {
                if (event instanceof InteractEntityEvent.Primary && !checkEntityPerms(player, event, Permissions.BUILD))
                    event.setCancelled(true);
                else if (event instanceof InteractEntityEvent.Secondary) {
                    if (Core.getCuboHandler().get(location.getBlockPosition()) != null) {
                        if (!checkCubo(dbplayer, location.getBlockPosition(), Permissions.CONTAINER)) {
                            player.sendMessage(message);
                            event.setCancelled(true);
                        }
                    } else {
                        if (!checkPerm(dbplayer, location.getBlockPosition(), world, Permissions.CONTAINER)) {
                            player.sendMessage(message);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Listener
    public void             onInteractBlockSecondaryMainhand(InteractBlockEvent.Secondary.MainHand event, @First Player player) {
        BlockType           block = event.getTargetBlock().getState().getType();

        if (event.getTargetBlock().getLocation().isPresent() && !block.equals(BlockTypes.AIR))
            this.handleEventInteract(event, event.getTargetBlock().getLocation().get(), player);
    }

    private void            handleEventInteract(InteractBlockEvent.Secondary.MainHand event, Location<World> location, Player player) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);
        World               world = player.getWorld();
        int                 x = location.getBlockX();
        int                 z = location.getBlockZ();
        ItemStack           itemStack = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null);
        Text                message = Text.of(TextColors.RED, "Vous ne pouvez pas effectuer cette action", TextColors.RESET);

        if (dbPlayer.getElapsedTimeClick() && itemStack != null && itemStack.getType().equals(ItemTypes.FEATHER)) {
            this.displayInfoFeather(dbPlayer, location, world);
            dbPlayer.setLastClick(Instant.now().getEpochSecond());
        }

        if (!dbPlayer.hasAdminMode() && Core.getChunkHandler().exists(x / 16, z / 16, world)) {
            if (SwitchableBlocks.contains(event.getTargetBlock().getState().getType())) {
                if (!checkCubo(dbPlayer, location.getBlockPosition(), "Switch") || !checkPerm(dbPlayer, location.getBlockPosition(), world, "Switch")) {
                    dbPlayer.sendMessage(message);
                    event.setCancelled(true);
                }
            } else if (ContainersBlock.contains(event.getTargetBlock().getState().getType())) {
                if (!checkCubo(dbPlayer, location.getBlockPosition(), "Container") || !checkPerm(dbPlayer, location.getBlockPosition(), world, "Container")) {
                    dbPlayer.sendMessage(message);
                    event.setCancelled(true);
                }
            }
        }
    }

    private boolean         allowTo(DBPlayer player, Vector3i blockPosition, World world, String permission) {
        if (Core.getCuboHandler().get(blockPosition) != null) {

        }
    }

    private void            displayInfoFeather(DBPlayer player, Location<World> location, World world) {
        Chunk chunk = Core.getChunkHandler().get(location.getBlockX() / 16, location.getBlockZ() / 16, world);
        Cubo cubo = Core.getCuboHandler().get(location.getBlockPosition());

        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "===| Parcelle [" + (location.getBlockX() / 16) + ";" + (location.getBlockZ() / 16) + "] |===", TextColors.RESET));
        player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Propriétaire : " + (chunk == null ? "Nature" : chunk.getCity().getDisplayName()), TextColors.RESET));
        if (cubo != null)
            player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "===| cubo '" + cubo.getName() + "' [" + cubo.getOwner().getDisplayName() + "] |===", TextColors.RESET));
    }
/*
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
                    player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "===| cubo '" + cubo.getName() + "' [" + cubo.getOwner().getDisplayName() + "] |===", TextColors.RESET));
            }


            if (!player.hasAdminMode() && Core.getChunkHandler().exists(x / 16, z / 16, world) && !Core.getWarHandler().isConcerned(location.getBlockPosition(), world)) {
                if (SwitchableBlocks.contains(event.getTargetBlock().getState().getType())) {
                    if (!checkCubo(player, location.getBlockPosition(), "Switch")) {
                        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas effectuer cette action", TextColors.RESET));
                        event.setCancelled(true);
                    } else if (!checkPerm(player, location.getBlockPosition(), world, "Switch")) {
                        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas effectuer cette action", TextColors.RESET));
                        event.setCancelled(true);
                    }
                } else if (ContainersBlock.contains(event.getTargetBlock().getState().getType())) {
                    if (!checkCubo(player, location.getBlockPosition(), "Container")) {
                        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas effectuer cette action", TextColors.RESET));
                        event.setCancelled(true);
                    } else if (!checkPerm(player, location.getBlockPosition(), world, "Container")) {
                        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas effectuer cette action", TextColors.RESET));
                        event.setCancelled(true);
                    }
                    event.setCancelled(true);
                }
            }
        }
    } */

    @Listener(order = Order.FIRST)
    public void             onBlockExplosion(ExplosionEvent event) {
        ((Cancellable) event).setCancelled(true);
    }

    private boolean         checkCubo(DBPlayer player, Vector3i pos, String permission) {
        Cubo cubo = Core.getCuboHandler().get(pos);
        List<DBPlayer> list = new ArrayList<>();

        if (cubo != null) {
            list.add(cubo.getOwner());
            list.addAll(cubo.getCity().getAssistants());
            list.add(cubo.getCity().getOwner());
            if (cubo.getLoan() != null && cubo.getLoan().getLoaner() != null)
                list.add(cubo.getLoan().getLoaner());

            if (list.contains(player))
                return true;

            if (cubo.getInList().contains(player)) {
                if (!Core.getPermissionHandler().ableToInList(player, cubo, permission, pos))
                    return Core.getWarHandler().Contains(player) && Core.getWarHandler().ableTo(player, cubo);
            } else {
                if (!Core.getPermissionHandler().ableToOutList(player, cubo, permission, pos))
                    return Core.getWarHandler().Contains(player) && Core.getWarHandler().ableTo(player, cubo);
            }
        }
        return true;
    }

    private boolean         checkCuboPerms(Player player, ChangeBlockEvent event, String permission) {
        DBPlayer            dbPlayer = Core.getPlayerHandler().get(player);

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!checkCubo(dbPlayer, transaction.getOriginal().getPosition(), permission))
                return false;
        }
        return true;
    }

    private boolean         checkEntityPerms(Player player, InteractEntityEvent event, String perm) {
        DBPlayer dbPlayer = Core.getPlayerHandler().get(player);

        return checkPerm(dbPlayer, event.getTargetEntity().getLocation().getBlockPosition(), player.getWorld(), perm);
    }

    private boolean         checkChunkPerms(Player player, ChangeBlockEvent event, String perm) {
        DBPlayer dbPlayer = Core.getPlayerHandler().get(player);

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!checkPerm(dbPlayer, transaction.getOriginal().getLocation().get().getBlockPosition(),
                    player.getWorld(), perm)) {
                return false;
            }
        }
        return true;
    }

    private boolean         checkPerm(DBPlayer dbPlayer, Vector3i pos, World world, String perm) {
        Chunk chunk = Core.getChunkHandler().get(pos.getX() / 16, pos.getZ() / 16, world);

        if (chunk != null) {
            if (!Core.getPermissionHandler().ableTo(dbPlayer, chunk, perm, pos)) {
                return Core.getWarHandler().Contains(dbPlayer) && Core.getWarHandler().ableTo(dbPlayer, chunk);
            }
        }
        return true;
    }
}