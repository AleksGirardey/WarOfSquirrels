package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.admin.CustomReward;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WorldInteractionHandler {
    private static final List<Block> farmBlocks = new ArrayList<>();
    private static final List<TagKey<Block>> switchTags = new ArrayList<>();
    private static final List<Item> overpassSwitchItems = new ArrayList<>();
    private static final List<Item> overpassSwitchItemsInWar = new ArrayList<>();

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

        switchTags.add(BlockTags.DOORS);
        switchTags.add(BlockTags.TRAPDOORS);
        switchTags.add(BlockTags.FENCE_GATES);
        switchTags.add(BlockTags.BUTTONS);
        switchTags.add(BlockTags.WOODEN_BUTTONS);

        overpassSwitchItems.add(Items.POTION);
        overpassSwitchItems.add(Items.SPLASH_POTION);
        overpassSwitchItems.add(Items.ENDER_PEARL);

        overpassSwitchItemsInWar.addAll(overpassSwitchItems);
        overpassSwitchItemsInWar.add(Items.WATER_BUCKET);
        overpassSwitchItemsInWar.add(Items.LAVA_BUCKET);
        overpassSwitchItemsInWar.add(Items.BUCKET);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnBlockDestroy(BlockEvent.BreakEvent event) {
        // Deal with sign shop

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (!ShouldWeCheck(player, new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) return;

        PermissionHandler.Rights rights = (player.isInWar() ?
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
            player.sendMessage(ChatText.Colored("You cannot destroy here", ChatFormatting.RED)
                    .withStyle(ChatFormatting.BOLD));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Player playerEntity)) {
            return;
        }

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (!ShouldWeCheck(player, new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) return;

        PermissionHandler.Rights rights = (player.isInWar() ?
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
            if (farmBlocks.contains(event.getState().getBlock())) {
                return;
            }

            player.sendMessage(ChatText.Error("You cannot build here").withStyle(ChatFormatting.BOLD));
            event.setCanceled(true);
        }
    }

    enum InteractType { None, RightClickBlock, RightClickItem, RightClickEntity, LeftClick }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void OnPlayerInteractEvent(PlayerInteractEvent event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getEntity().getUUID());
        InteractType type;

        if (event instanceof PlayerInteractEvent.LeftClickBlock)
            type = InteractType.LeftClick;
        else if (event instanceof PlayerInteractEvent.RightClickBlock)
            type = InteractType.RightClickBlock;
        else if (event instanceof PlayerInteractEvent.RightClickItem)
            type = InteractType.RightClickItem;
        else if (event instanceof  PlayerInteractEvent.EntityInteract)
            type = InteractType.RightClickEntity;
        else { return; }

        Vector3 click = new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());

        if (WarOfSquirrels.instance.getCuboHandler().playerExists(player)) {
            WarOfSquirrels.instance.getCuboHandler().set(player, click,
                    type == InteractType.LeftClick);
            event.setCanceled(true);
        }

        CustomReward reward = WarOfSquirrels.instance.getRewardHandler().contains(click);

        if (reward != null) {
            if (reward.CanAddRewardedPlayer()) {
                if (reward.AddRewardedPlayer(player)) {
                    player.getRewards().add(reward);
                    player.sendMessage(ChatText.Success("You obtain a new reward !"));
                } else {
                    player.sendMessage(ChatText.Success("You cannot claim this reward again"));
                }
            } else {
                player.sendMessage(ChatText.Error("No reward available"));
            }
            event.setCanceled(true);
            return;
        }

        if (type == InteractType.RightClickItem) {
            Item item = event.getItemStack().getItem();
            if (item.isEdible()) return;

            if (item == Items.FEATHER) {
                Utils.displayInfoFeather(event.getEntity(), event.getEntity().getOnPos(), event.getLevel().dimension());
                event.setCanceled(true);
            } else if (item == Items.BOW || item == Items.CROSSBOW || item == Items.SHIELD || player.isAdminMode()) {
                return;
            }
        }

        if (!ShouldWeCheck(player, new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) return;

        OnPlayerInteract(player, event, type);
    }

    private void OnPlayerInteract(FullPlayer player, PlayerInteractEvent event, InteractType type) {
        if (type == InteractType.LeftClick) { return; }
        if (type == InteractType.RightClickEntity) {
            if (WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.INTERACT,
                    new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                    Chunk.DimensionToId(player.getLastDimensionKey()),
                    player)) {
                return;
            }

            event.getEntity().sendSystemMessage(ChatText.Error("You do not have the permission to interact with this entity"));
            event.setCanceled(true);
        } else if (type == InteractType.RightClickBlock) {
            boolean isSwitch = false;

            PlayerInteractEvent.RightClickBlock blockEvent = (PlayerInteractEvent.RightClickBlock) event;

            for (TagKey<Block> tag : switchTags) {
                if (event.getLevel().getBlockState(blockEvent.getPos()).is(tag)) {
                    isSwitch = true;
                    break;
                }
            }

            if (!isSwitch) {
                String lastDimensionId = Chunk.DimensionToId(player.getLastDimensionKey());

                if (IsContainer(blockEvent.getLevel(), blockEvent.getPos(), null)) {
                    if (!OnPlayerContainer(event.getEntity(), event.getPos(), lastDimensionId)) {
                        player.sendMessage(ChatText.Error("You do not have permission to open this container."));
                        event.setCanceled(true);
                    }
                    return;
                } else if (event.getEntity().getMainHandItem().getItem() instanceof HangingEntityItem ||
                        event.getEntity().getMainHandItem().getItem() instanceof ArmorStandItem) {

                    boolean canConstruct = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                            PermissionHandler.Rights.BUILD,
                            new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                            Chunk.DimensionToId(player.getLastDimensionKey()),
                            player);

                    if (!canConstruct) {
                        player.sendMessage(ChatText.Error("You cannot build here").withStyle(ChatFormatting.BOLD));
                        event.setCanceled(true);
                        return;
                    }
                }
                return;
            }
        }

        boolean chunkInWar = WarOfSquirrels.instance.getWarHandler().Contains(WarOfSquirrels.instance.getChunkHandler().getChunk(
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()), event.getLevel().dimension()).getRelatedCity());

        if (CanOverpassSwitchPermission(event.getItemStack(), chunkInWar)) return;

        if (WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(PermissionHandler.Rights.SWITCH,
                new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                Chunk.DimensionToId(player.getLastDimensionKey()),
                player)) {
            return;
        }

        event.getEntity().sendSystemMessage(ChatText.Error("You do not have the permission to interact with this block"));
        event.setCanceled(true);
    }

    private boolean CanOverpassSwitchPermission(ItemStack itemStack, boolean isInWar) {
//        if (itemStack.isEdible()) return true;

        List<Item> targetList = isInWar ? overpassSwitchItemsInWar : overpassSwitchItems;

        for (Item itemAllowed : targetList) {
            if (itemStack.is(itemAllowed)) return true;
        }

        return false;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnEntityAttack(AttackEntityEvent event) {
        if (event.getTarget() instanceof HangingEntity || event.getTarget() instanceof ArmorStand) {
            FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getEntity().getUUID());

            if (player.isAdminMode()) return;

            boolean canBuild = WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(
                    PermissionHandler.Rights.BUILD,
                    new Vector3(
                            event.getTarget().getBlockX(),
                            event.getTarget().getBlockY(),
                            event.getTarget().getBlockZ()),
                    Chunk.DimensionToId(event.getEntity().getCommandSenderWorld().dimension()),
                    player);

            if (!canBuild) {
                event.setCanceled(true);
                player.sendMessage(ChatText.Error("You do not have permission to do this."));
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLivingHurtEntity(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer) return;

        if (!(event.getSource().getEntity() instanceof ServerPlayer sourcePlayer)) return;

        if (event.getEntity() instanceof Monster) { return; }

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
    public void OnLivingDeath(LivingDeathEvent event) {
        FullPlayer player;

        if (event.getEntity() instanceof ServerPlayer) {
            player = WarOfSquirrels.instance.getPlayerHandler().get(event.getEntity().getUUID());

            player.setDeathCount(player.getDeathCount() + 1);
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer sourcePlayer)) return;

        player = WarOfSquirrels.instance.getPlayerHandler().get(sourcePlayer.getUUID());

        if (event.getEntity() instanceof Monster) {
            player.setMonsterKillCount(player.getMonsterKillCount() + 1);
        }
        else if (event.getEntity() instanceof Mob) {
            player.setMobKillCount(player.getMobKillCount() + 1);
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

        return state.getBlock() instanceof Container || world.getBlockEntity(pos) instanceof BaseContainerBlockEntity;
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void OnFarmLand(BlockEvent event) {
        FullPlayer player;

        if (event instanceof BlockEvent.BreakEvent breakEvent) {
            player = WarOfSquirrels.instance.getPlayerHandler().get(breakEvent.getPlayer().getUUID());

            if (player.isAdminMode() || !farmBlocks.contains(event.getState().getBlock())) return;

            if (!ShouldWeCheck(player, new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) return;

            if (WarOfSquirrels.instance.getPermissionHandler()
                    .hasRightsTo(PermissionHandler.Rights.FARM,
                            new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()),
                            player.getLastDimension(), player)) return;

            event.setCanceled(true);
            player.sendMessage(ChatText.Error("You cannot farm this."));
            return;
        }
        if (event instanceof BlockEvent.BlockToolModificationEvent toolEvent) {
            if (toolEvent.getPlayer() == null) return;

            player = WarOfSquirrels.instance.getPlayerHandler().get(toolEvent.getPlayer().getUUID());
            if (player.isAdminMode()) return;

            if (!ShouldWeCheck(player, new Vector3(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ()))) return;

            PermissionHandler.Rights rights;
            if (toolEvent.getToolAction() == ToolActions.HOE_DIG)
                rights = PermissionHandler.Rights.BUILD;
            else if (toolEvent.getToolAction() == ToolActions.HOE_TILL)
                rights = PermissionHandler.Rights.FARM;
            else
                return;

            if (WarOfSquirrels.instance.getPermissionHandler().hasRightsTo(rights,
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

    @SubscribeEvent
    public void OnExplosion(ExplosionEvent.Start event) {
        if (event.getExplosion().getDamageSource().getEntity() instanceof Monster) return;

        event.setCanceled(true);
    }

    @SubscribeEvent
    public void OnMobGriefing(EntityMobGriefingEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Villager || entity instanceof Sheep || entity instanceof Evoker) return;

        event.setResult(Event.Result.DENY);
    }

    private boolean ShouldWeCheck(FullPlayer player, Vector3 target) {
        if (player.isAdminMode()) return false;

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(target, player.getLastDimensionKey());

        boolean hasChunk = chunk != null;

        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(target);

        boolean hasCubo = cubo != null;

        AdminCubo adminCubo = WarOfSquirrels.instance.getAdminHandler().get(target, player.getLastDimensionKey());

        boolean hasAdminCubo = adminCubo != null;

        return hasCubo || hasChunk || hasAdminCubo;
    }
}
