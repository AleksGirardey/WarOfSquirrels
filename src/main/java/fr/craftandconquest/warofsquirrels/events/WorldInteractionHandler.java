package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldInteractionHandler {

    private final Logger logger;

    public WorldInteractionHandler(Logger logger) {
        this.logger = logger;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        List<BroadCastTarget> targets = new ArrayList<>();
        Player playerEntity = event.getPlayer();

        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();

        FullPlayer player;
        
        if (!playerHandler.exists(playerEntity.getUUID(), true))
            player = playerHandler.CreatePlayer(playerEntity);
        else {
            player = playerHandler.get(playerEntity.getUUID());
            player.setChatTarget(BroadCastTarget.GENERAL);
            player.lastDimension = event.getPlayer().getCommandSenderWorld().dimension();
            player.lastPosition = new Vector3(
                    (float) event.getPlayer().getBlockX(),
                    (float) event.getPlayer().getBlockY(),
                    (float) event.getPlayer().getBlockZ());
        }

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
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (player == null) return;

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerToWorldAnnounce(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTargets(player);

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedOut] Player %s removed from all channels",
                player.getDisplayName()));
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (WarOfSquirrels.instance.getCuboHandler().playerExists(player)) {
            event.setCanceled(true);
            WarOfSquirrels.instance.getCuboHandler().set(
                    player,
                    new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                    true);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnBlockDestroy(BlockEvent.BreakEvent event) {
        // Deal with sign shop

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (player.isAdminMode()) return;

        PermissionHandler.Rights rights = (WarOfSquirrels.instance.getWarHandler().Contains(player) ?
                PermissionHandler.Rights.DESTROY_IN_WAR :
                PermissionHandler.Rights.BUILD);

        boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                rights,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                Chunk.DimensionToId(player.lastDimension),
                player,
                event.getState().getBlock());

        if (!canConstruct) {
            event.setCanceled(true);
            player.sendMessage(new TextComponent("You cannot destroy here")
                    .withStyle(ChatFormatting.BOLD)
                    .withStyle(ChatFormatting.RED));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player playerEntity)) {
            return;
        }

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player.isAdminMode()) return;

        PermissionHandler.Rights rights = (WarOfSquirrels.instance.getWarHandler().Contains(player) ?
                PermissionHandler.Rights.PLACE_IN_WAR :
                PermissionHandler.Rights.BUILD);

        boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                rights,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                Chunk.DimensionToId(player.lastDimension),
                player,
                event.getPlacedBlock().getBlock());

        if (!canConstruct) {
            event.setCanceled(true);
            player.sendMessage(
                    ChatText.Error("You cannot build here").withStyle(ChatFormatting.BOLD));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLivingHurtEntity(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer sourcePlayer)) return;

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(sourcePlayer.getUUID());

        boolean canFarm = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.FARM,
                new Vector3(
                        (int) sourcePlayer.position().x,
                        (int) sourcePlayer.position().y,
                        (int) sourcePlayer.position().z),
                Chunk.DimensionToId(sourcePlayer.getCommandSenderWorld().dimension()),
                player);

        if (!canFarm) {
            event.setCanceled(true);
            player.sendMessage(ChatText.Error("You have no right to interact with this entity."));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        WarOfSquirrels.LOGGER.info("INTERACT WITH " + event.getHand().name());


        int lastDimensionId = Chunk.DimensionToId(player.lastDimension);

        if (WarOfSquirrels.instance.getCuboHandler().playerExists(player)) {
            if (event.getHand().equals(InteractionHand.OFF_HAND)) return;
            event.setCanceled(true);
            WarOfSquirrels.instance.getCuboHandler().set(
                    player,
                    new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                    false);
            return;
        } else if (IsContainer(event.getWorld(), event.getPos(), null, null)) {
            if (OnPlayerContainer(event.getPlayer(), event.getPos(), lastDimensionId))
                return;
        } else if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), lastDimensionId))
            return;

        event.getPlayer().sendMessage(ChatText.Error("You do not have the permission to interact with this block"), Util.NIL_UUID);
        event.setCanceled(true);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() == Items.FEATHER) {
            displayInfoFeather(event.getPlayer(), event.getPlayer().getOnPos(), event.getWorld().dimension());
            event.setCanceled(true);
            return;
        }

        if (HandlePlayerRightClick(event.getPlayer(), event.getPos(), Chunk.DimensionToId(event.getWorld().dimension()))) return;

        event.getPlayer().sendMessage(ChatText.Error("You have not the permission to interact with this item"), Util.NIL_UUID);
        event.setCanceled(true);
    }

    private void displayInfoFeather(Player player, BlockPos pos, ResourceKey<Level> dimensionId) {
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(
                pos.getX() / WarOfSquirrels.instance.getConfig().getTerritorySize(),
                pos.getZ() / WarOfSquirrels.instance.getConfig().getTerritorySize());

        if (territory == null) return;

        int posX = pos.getX();
        int posZ = pos.getZ();
        ChunkPos chunkPos = Utils.WorldToChunkPos(posX, posZ);

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkPos.x, chunkPos.z, dimensionId);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(new Vector3(posX, pos.getY(), posZ));

        player.sendMessage(ChatText.Colored(MessageFormat.format(
                """
                        ===| Territory {0} [{1};{2}] |===
                            Owner : {3}
                        ===| Chunk [{4};{5}] |===
                            Owner : {6}""",
                territory.getName(), territory.getPosX(), territory.getPosZ(),
                (territory.getFaction() == null ? "None" : territory.getFaction().getDisplayName()),
                chunkPos.x, chunkPos.z,
                (chunk == null ? "None" : chunk.getCity().getDisplayName())),
                ChatFormatting.LIGHT_PURPLE), Util.NIL_UUID);

        if (cubo != null) {
            player.sendMessage(ChatText.Colored("===| Cubo '" + cubo.getName() + "' [" + cubo.getOwner().getDisplayName() + "] |===", ChatFormatting.LIGHT_PURPLE), Util.NIL_UUID);
        }
    }

    private boolean HandlePlayerRightClick(Player playerEntity, BlockPos target, int dimensionId) {
        Vector3 position = new Vector3(target.getX(), target.getY(), target.getZ());
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.SWITCH,
                position, dimensionId, player);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnCropsTrampled(BlockEvent.FarmlandTrampleEvent event) {
        event.setCanceled(true);
    }

    public boolean OnPlayerContainer(Player playerEntity, BlockPos target, int dimensionId) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player.isAdminMode()) return true;

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.CONTAINER,
                new Vector3(target.getX(), target.getY(), target.getZ()),
                dimensionId,
                player);
    }

    public static boolean IsContainer(Level world, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity tileEntity) {
        if (state == null)
            state = world.getBlockState(pos);
        if (tileEntity == null)
            tileEntity = world.getBlockEntity(pos);
        return state.getBlock() instanceof Container;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnServerShuttingDown(FMLServerStoppingEvent event) {
        WarOfSquirrels.instance.getUpdateHandler().SaveTask();
        WarOfSquirrels.instance.getUpdateHandler().CancelTask();
    }

//    @OnlyIn(Dist.DEDICATED_SERVER)
//    @SubscribeEvent
//    public void OnLivingSpawnEvent(LivingSpawnEvent event) {
//        Entity entity = event.getEntity();
//
//        if (!WarOfSquirrels.instance.getIsModInit() || !(entity instanceof Mob)) return;
//
//        boolean canSpawn = WarOfSquirrels.instance.getChunkHandler().getChunk(
//                entity.getOnPos().getX(),
//                entity.getOnPos().getZ(),
//                entity.level.dimension()) == null;
//
//        if (!canSpawn) {
//            event.setCanceled(true);
//        }
//    }
}
