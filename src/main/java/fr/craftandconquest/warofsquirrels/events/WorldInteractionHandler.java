package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldInteractionHandler {

    private static final List<Block> farmBlocks = new ArrayList<>();

    static {
        farmBlocks.add(Blocks.WHEAT);
        farmBlocks.add(Blocks.CARROTS);
        farmBlocks.add(Blocks.POTATOES);
        farmBlocks.add(Blocks.BEETROOTS);
        farmBlocks.add(Blocks.PUMPKIN);
        farmBlocks.add(Blocks.PUMPKIN_STEM);
        farmBlocks.add(Blocks.ATTACHED_PUMPKIN_STEM);
        farmBlocks.add(Blocks.MELON);
        farmBlocks.add(Blocks.MELON_STEM);
        farmBlocks.add(Blocks.ATTACHED_MELON_STEM);
        farmBlocks.add(Blocks.FARMLAND);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (player.isAdminMode()) return;

        enum InteractType { None, RightClickEmpty, RightClickItem, RightClickEntity }

        InteractType type;

        if (event instanceof PlayerInteractEvent.LeftClickBlock || event instanceof PlayerInteractEvent.LeftClickEmpty)
            type = InteractType.None;
        else if (event instanceof PlayerInteractEvent.RightClickBlock || event instanceof PlayerInteractEvent.RightClickEmpty)
            type = InteractType.RightClickEmpty;
        else if (event instanceof PlayerInteractEvent.RightClickItem)
            type = InteractType.RightClickItem;
        else if (event instanceof  PlayerInteractEvent.EntityInteract)
            type = InteractType.RightClickEntity;
        else
            return;

        if (type == InteractType.None) return;

        if (type == InteractType.RightClickItem) {
            Item item = event.getItemStack().getItem();
            if (item == Items.FEATHER) {
                Utils.displayInfoFeather(event.getPlayer(), event.getPlayer().getOnPos(), event.getWorld().dimension());
                event.setCanceled(true);
                return;
            } else if (item == Items.BOW || item == Items.CROSSBOW) {
                return;
            }
        } else if (type == InteractType.RightClickEntity) {
            if (WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.INTERACT,
                    new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                    Chunk.DimensionToId(player.getLastDimensionKey()),
                    player)) return;

            event.getPlayer().sendMessage(ChatText.Error("You do not have the permission to interact with this entity"), Util.NIL_UUID);
            event.setCanceled(true);
        }

        String lastDimensionId = Chunk.DimensionToId(player.getLastDimensionKey());

        if (IsContainer(event.getWorld(), event.getPos(), null)) {
            if (OnPlayerContainer(event.getPlayer(), event.getPos(), lastDimensionId))
                return;
        } else if (WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.SWITCH,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                Chunk.DimensionToId(player.getLastDimensionKey()),
                player))
            return;

        event.getPlayer().sendMessage(ChatText.Error("You do not have the permission to interact with this block"), Util.NIL_UUID);
        event.setCanceled(true);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
                Chunk.DimensionToId(player.getLastDimensionKey()),
                player,
                event.getState(),
                event.getPos());

        if (!canConstruct) {
            event.setCanceled(true);
            player.sendMessage(new TextComponent("You cannot destroy here")
                    .withStyle(ChatFormatting.BOLD)
                    .withStyle(ChatFormatting.RED));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
                Chunk.DimensionToId(player.getLastDimensionKey()),
                player,
                event.getState(),
                event.getPos());

        if (!canConstruct) {
            if (farmBlocks.contains(event.getState().getBlock())) return;

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

        if (event.getEntityLiving() instanceof Monster) { return; }

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

    public boolean OnPlayerContainer(Player playerEntity, BlockPos target, String dimensionId) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player.isAdminMode()) return true;

        return WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                PermissionHandler.Rights.CONTAINER,
                new Vector3(target.getX(), target.getY(), target.getZ()),
                dimensionId,
                player);
    }

    public static boolean IsContainer(Level world, BlockPos pos, @Nullable BlockState state) {
        if (state == null)
            state = world.getBlockState(pos);
        return state.getBlock() instanceof Container;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnFarmLand(BlockEvent event) {
        FullPlayer player;

        if (event instanceof BlockEvent.BreakEvent breakEvent) {
            player = WarOfSquirrels.instance.getPlayerHandler().get(breakEvent.getPlayer().getUUID());

            if (player.isAdminMode() || !farmBlocks.contains(event.getState().getBlock())) return;

            if (WarOfSquirrels.instance.getPermissionHandler()
                    .hasRightsTo(PermissionHandler.Rights.FARM,
                            new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                            player.getLastDimension(), player)) return;

            event.setCanceled(true);
            player.sendMessage(ChatText.Error("You cannot farm this."));
            return;
        }
        if (event instanceof BlockEvent.BlockToolInteractEvent toolEvent) {
            player = WarOfSquirrels.instance.getPlayerHandler().get(toolEvent.getPlayer().getUUID());
            if (player.isAdminMode()) return;

            if (toolEvent.getToolAction().equals(ToolActions.HOE_DIG) && WarOfSquirrels.instance.getPermissionHandler()
                    .hasRightsTo(PermissionHandler.Rights.FARM,
                            new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                            player.getLastDimension(), player)) return;
            event.setCanceled(true);
            player.sendMessage(ChatText.Error("You cannot use your hoe here."));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnCropsTrampled(BlockEvent.FarmlandTrampleEvent event) {
        event.setCanceled(true);
    }
}
