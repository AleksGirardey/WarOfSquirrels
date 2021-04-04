package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class WorldInteractionHandler {

    private final Logger logger;

    public WorldInteractionHandler(Logger logger){
        this.logger = logger;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        List<BroadCastTarget> targets = new ArrayList<>();
        PlayerEntity playerEntity = event.getPlayer();

        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();

        Player player;

        if (!playerHandler.exists(playerEntity))
            player = playerHandler.CreatePlayer(playerEntity);
        else
            player = playerHandler.get(playerEntity);

        broadCastHandler.AddPlayerToWorldAnnounce(player);
        targets.add(BroadCastTarget.GENERAL);

        if (player.getCity() != null) {
            broadCastHandler.AddPlayerToTarget(player.getCity(), player);
            targets.add(player.getCity().getBroadCastTarget());
            if (player.getCity().getFaction() != null) {
                broadCastHandler.AddPlayerToTarget(player.getCity().getFaction(), player);
                targets.add(player.getCity().getFaction().getBroadCastTarget());
            }
        }

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedIn] Player %s added to %s channels",
                player.getDisplayName(), targets.size()));

        // Ajouter les canaux de support et d'admin
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void PlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        if (player == null) return;

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerToWorldAnnounce(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTargets(player);

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedOut] Player %s removed from all channels",
                player.getDisplayName()));
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnBlockDestroy(BlockEvent.BreakEvent event) {
        // Deal with sign shop

        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        if (WarOfSquirrels.instance.getCuboHandler().playerExists(player)) {
            event.setCanceled(true);
            WarOfSquirrels.instance.getCuboHandler().set(
                    player,
                    new Vector3(event.getPos().getX(),event.getPos().getY(),event.getPos().getZ()),
                    true);
            return;
        }

        if (PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op")) return;

        PermissionHandler.Rights rights = (WarOfSquirrels.instance.getWarHandler().Contains(player) ?
                PermissionHandler.Rights.DESTROY_IN_WAR :
                PermissionHandler.Rights.BUILD);

        boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                rights,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                event.getWorld().getDimension().getType().getId(),
                player,
                event.getState().getBlock());

        if (!canConstruct) {
            event.setCanceled(true);
            player.getPlayerEntity().sendMessage(new StringTextComponent("You cannot destroy here")
                    .applyTextStyle(TextFormatting.BOLD)
                    .applyTextStyle(TextFormatting.RED));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity)) {
            return;
        }

        Player player = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getEntity());

        if (PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op")) return;

        PermissionHandler.Rights rights = (WarOfSquirrels.instance.getWarHandler().Contains(player) ?
                PermissionHandler.Rights.PLACE_IN_WAR :
                PermissionHandler.Rights.BUILD);

        boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                rights,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                event.getWorld().getDimension().getType().getId(),
                player,
                event.getPlacedBlock().getBlock());

        if (!canConstruct) {
            event.setCanceled(true);
            player.getPlayerEntity().sendMessage(new StringTextComponent("You cannot build here")
                    .applyTextStyle(TextFormatting.BOLD)
                    .applyTextStyle(TextFormatting.RED));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLivingHurtEntity(LivingHurtEvent event) {
        if (event.getEntity() instanceof PlayerEntity) return;

        if (!(event.getSource().getTrueSource() instanceof PlayerEntity)) return;

        Player player = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getSource().getTrueSource());

        boolean canFarm = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.FARM,
                new Vector3(
                        (int) event.getEntity().lastTickPosX,
                        (int) event.getEntity().lastTickPosY,
                        (int) event.getEntity().lastTickPosZ),
                event.getEntity().dimension.getId(),
                player);

        if (!canFarm) {
            event.setCanceled(true);
            player.getPlayerEntity().sendMessage(
                    new StringTextComponent("You have no right no interact with this entity.")
                            .applyTextStyle(TextFormatting.RED));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        if (WarOfSquirrels.instance.getCuboHandler().playerExists(player)) {
            event.setCanceled(true);
            WarOfSquirrels.instance.getCuboHandler().set(
                    player,
                    new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                    false);
        } else if (IsContainer(event.getWorld(), event.getPos(), null, null)) {
            if (OnPlayerContainer(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId()))
                return;
        } else if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId())) return;

        event.getPlayer().sendMessage(new StringTextComponent("You have not the permission to interact with this block")
                .applyTextStyle(TextFormatting.RED));
        event.setCanceled(true);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.FEATHER) {
            displayInfoFeather(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId());
            event.setCanceled(true);
            return;
        }

        if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId())) return;

        event.getPlayer().sendMessage(new StringTextComponent("You have not the permission to interact with this item")
                .applyTextStyle(TextFormatting.RED));
        event.setCanceled(true);
    }

    private void displayInfoFeather(PlayerEntity player, BlockPos pos, int dimensionId) {
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(
                pos.getX() / WarOfSquirrels.instance.getConfig().getTerritorySize(),
                pos.getZ() / WarOfSquirrels.instance.getConfig().getTerritorySize(), dimensionId);
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(pos.getX() / 16, pos.getZ() / 16, dimensionId);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(
                new Vector3(pos.getX(), pos.getY(), pos.getZ()));

        player.sendMessage(new StringTextComponent("===| Territory " + territory.getName() + "[" + territory.getPosX() + ";" + territory.getPosZ() + "] |===")
                        .applyTextStyle(TextFormatting.LIGHT_PURPLE)
                        .applyTextStyle(TextFormatting.BOLD));
        player.sendMessage(new StringTextComponent("Owner : " + (territory.getFaction() == null ? "Wilderness" : territory.getFaction().getDisplayName()))
                        .applyTextStyle(TextFormatting.LIGHT_PURPLE)
                        .applyTextStyle(TextFormatting.BOLD));
        player.sendMessage(new StringTextComponent("===| Chunk [" + (pos.getX() / 16) + ";" + (pos.getZ() / 16) + "] |===")
                        .applyTextStyle(TextFormatting.LIGHT_PURPLE)
                        .applyTextStyle(TextFormatting.BOLD));
        player.sendMessage(new StringTextComponent("Owner : " + (chunk == null ? "Nature" : chunk.getCity().getDisplayName()))
                        .applyTextStyle(TextFormatting.LIGHT_PURPLE)
                        .applyTextStyle(TextFormatting.BOLD));
        if (cubo != null) {
            player.sendMessage(new StringTextComponent("===| Cubo '" + cubo.getName() + "' [" + cubo.getOwner().getDisplayName() + "] |===")
                    .applyTextStyle(TextFormatting.LIGHT_PURPLE)
                    .applyTextStyle(TextFormatting.BOLD));
        }
    }

    private boolean HandlePlayerRightClick(PlayerEntity playerEntity, BlockPos target, int dimensionId) {
        Vector3 position = new Vector3(target.getX(), target.getY(), target.getZ());
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.SWITCH,
                position, dimensionId, player);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnCropsTrampled(BlockEvent.FarmlandTrampleEvent event) {
        event.setCanceled(true);
    }

    public boolean OnPlayerContainer(PlayerEntity playerEntity, BlockPos target, int dimensionId) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        if (PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op")) return true;

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.CONTAINER,
                new Vector3(target.getX(), target.getY(), target.getZ()),
                dimensionId,
                player);
    }

    public static boolean IsContainer(World world, BlockPos pos, @Nullable BlockState state, @Nullable TileEntity tileEntity) {
        if(state == null)
            state = world.getBlockState(pos);
        if(tileEntity == null)
            tileEntity = world.getTileEntity(pos);
        return state.getBlock() instanceof ContainerBlock || tileEntity instanceof IInventory;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLivingSpawnEvent(LivingSpawnEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof MobEntity)) return;

        boolean canSpawn = WarOfSquirrels.instance.getChunkHandler().getChunk(
                entity.getPosition().getX(),
                entity.getPosition().getZ(),
                entity.dimension.getId()) == null;

        if (!canSpawn) {
            event.setCanceled(true);
        }
    }
}
