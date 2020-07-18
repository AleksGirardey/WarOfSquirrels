package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WorldInteractionHandler {

    private final Logger logger;

    public WorldInteractionHandler(Logger logger){
        this.logger = logger;
    }

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

    @SubscribeEvent
    public void PlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerToWorldAnnounce(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTargets(player);

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedOut] Player %s removed from all channels",
                player.getDisplayName()));
    }

    @SubscribeEvent
    public void OnBlockDestroy(BlockEvent.BreakEvent event) {
        // Deal with sign shop

        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        HandleEventConstruction(event, player, "You cannot destroy here");
    }

    @SubscribeEvent
    public void OnBlockPlace(BlockEvent.BreakEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());
        HandleEventConstruction(event, player, "You cannot build here");
    }

    private void HandleEventConstruction(BlockEvent event, Player player, String failedMessage) {
        if (PermissionAPI.hasPermission(player.getPlayerEntity(), "minecraft.command.op")) return;

        boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.BUILD,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                event.getWorld().getDimension().getType().getId(),
                player);

        if (!canConstruct) {
            event.setCanceled(true);
            player.getPlayerEntity().sendMessage(new StringTextComponent(failedMessage)
                    .applyTextStyle(TextFormatting.BOLD)
                    .applyTextStyle(TextFormatting.RED));
        }
    }

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

    @SubscribeEvent
    public void OnPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId())) return;

        event.getPlayer().sendMessage(new StringTextComponent("You have not the permission to interact with this block")
                .applyTextStyle(TextFormatting.RED));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void OnPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), event.getWorld().dimension.getType().getId())) return;

        event.getPlayer().sendMessage(new StringTextComponent("You have not the permission to interact with this item")
                .applyTextStyle(TextFormatting.RED));
        event.setCanceled(true);
    }

    private boolean HandlePlayerRightClick(PlayerEntity playerEntity, BlockPos target, int dimensionId) {
        Vector3 position = new Vector3(target.getX(), target.getY(), target.getZ());
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.SWITCH,
                position, dimensionId, player);
    }

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
