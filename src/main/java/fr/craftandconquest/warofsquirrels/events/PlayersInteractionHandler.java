package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.WarHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayersInteractionHandler {

//    @OnlyIn(Dist.DEDICATED_SERVER)
//    @SubscribeEvent
//    public void OnPlayerReSpawn(PlayerEvent.PlayerRespawnEvent event) {
//        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());
//
//        if (player.getCity() == null) return;
//
//        ReSpawnPoint spawnPoint = Utils.NearestSpawnPoint(event.getPlayer());
//        ServerPlayer serverPlayer = WarOfSquirrels.server.getPlayerList().getPlayer(player.getUuid());
//
//        if (serverPlayer != null) {
//            WarOfSquirrels.LOGGER.info("[WoS][Debug] Setting SpawnPoint on " + spawnPoint.position);
//            serverPlayer.setRespawnPosition(spawnPoint.dimension, spawnPoint.position, 0f, true, false);
////        event.getPlayer().teleportTo(spawnPoint.position.getX(), spawnPoint.position.getY(), spawnPoint.position.getZ());
//        }
//    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID())
                .setLastDimension(event.getTo().location().getPath());
        WarOfSquirrels.instance.getPlayerHandler().Save();
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerMove(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == null || !(event.getEntityLiving() instanceof Player playerEntity)) {
            return;
        }

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player == null) return;

        Vector3 newPosition = new Vector3(
                Math.round(playerEntity.getBlockX()),
                Math.round(playerEntity.getBlockY()),
                Math.round(playerEntity.getBlockZ()));

        ResourceKey<Level> dimensionId = playerEntity.getCommandSenderWorld().dimension();

        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(newPosition, dimensionId);

        if (cubo != null && cubo.isTeleporter()) {
            AdminCubo target = WarOfSquirrels.instance.getAdminHandler().get(cubo.getLinkedPortal());

            if (target != null) {
                SpawnTeleporter tp = new SpawnTeleporter(target.getRespawnPoint());
                ServerLevel level = WarOfSquirrels.server.getLevel(target.getDimensionKey());

                if (level == null) return;

                player.getPlayerEntity().changeDimension(level, tp);
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnChunkChange(EntityEvent.EnteringSection event) {
        if (!(event.getEntity() instanceof Player playerEntity)) return;

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player == null) return;

        player.setLastChunkX(event.getOldPos().chunk().x);
        player.setLastChunkZ(event.getOldPos().chunk().z);

        ResourceKey<Level> dimensionId = event.getEntity().getCommandSenderWorld().dimension();

        if (dimensionId != Level.OVERWORLD) return;

        Territory oldTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(new Vector2(event.getOldPos().chunk().x, event.getOldPos().chunk().z));
        Territory newTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(new Vector2(event.getNewPos().chunk().x, event.getNewPos().chunk().z));

        if (oldTerritory != newTerritory) {
            player.sendMessage(ChatText.Colored("~~| " + newTerritory.getName() + " |~~", ChatFormatting.DARK_GREEN));
        }

        Chunk lastChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(player.getLastChunkX(), player.getLastChunkZ(), dimensionId);
        Chunk newChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(event.getNewPos().chunk().x, event.getNewPos().chunk().z, dimensionId);

        if (lastChunk != null) {
            if (newChunk != null) {
                if (lastChunk.getRelatedCity() != newChunk.getRelatedCity()) {
                    if (WarOfSquirrels.instance.getWarHandler().IsConcerned(newChunk)) {
                        War war = WarOfSquirrels.instance.getWarHandler().getWar(newChunk.getRelatedCity());

                        if (!war.isDefender(player) && !war.isAttacker(player)) {
                            event.setCanceled(true);
                            return;
                        }
                    }

                    String place = newChunk.getFortification().getDisplayName();

                    if (newChunk.getFortification().getFortificationType() == IFortification.FortificationType.BASTION) {
                        place += (" - " + newChunk.getFortification().getRelatedCity().getDisplayName());
                    }

                    player.sendMessage(ChatText.Colored("~~| " + place + " |~~", ChatFormatting.GOLD));
                }
            } else {
                player.sendMessage(ChatText.Colored("~~| Wilderness |~~", ChatFormatting.GOLD));
            }
        } else {
            if (newChunk != null) {
                String place = newChunk.getFortification().getDisplayName();

                if (newChunk.getFortification().getFortificationType() == IFortification.FortificationType.BASTION) {
                    place += (" - " + newChunk.getFortification().getRelatedCity().getDisplayName());
                }
                player.sendMessage(ChatText.Colored("~~| " + place + " |~~", ChatFormatting.GOLD));
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPvPDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player target) || !(event.getSource().getEntity() instanceof Player))
            return;

        if (event.getSource().getEntity() != null)
            WarOfSquirrels.LOGGER.info("[Damage] " + event.getSource().getEntity().toString());

        FullPlayer fullPlayerTarget = WarOfSquirrels.instance.getPlayerHandler().get(target.getUUID());

        if (WarOfSquirrels.instance.getPlayerHandler().isInReincarnation(fullPlayerTarget))
            event.setAmount(10000);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPvPDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;

        if (!(event.getSource().getEntity() instanceof Player killer)) return;

        FullPlayer fullPlayerTarget = WarOfSquirrels.instance.getPlayerHandler().get(target.getUUID());
        FullPlayer fullPlayerKiller = WarOfSquirrels.instance.getPlayerHandler().get(killer.getUUID());

        WarOfSquirrels.instance.getPlayerHandler().SetReincarnation(fullPlayerTarget);
        WarHandler warHandler = WarOfSquirrels.instance.getWarHandler();

        if (warHandler.Contains(fullPlayerTarget) && warHandler.Contains(fullPlayerKiller)) {
            War war = warHandler.getWar(fullPlayerTarget);

            if (war.isAttacker(fullPlayerTarget) && war.isDefender(fullPlayerKiller))
                war.AddDefenderKillPoints();
            else if (war.isAttacker(fullPlayerKiller) && war.isDefender(fullPlayerTarget)) {
                if (war.isTarget(fullPlayerTarget))
                    war.AddAttackerTargetKillPoints();
                else
                    war.AddAttackerKillPoints();
            }
        }

        //ToDo: Add points leaderboard
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnChatEvent(ServerChatEvent event) {
        Component message = new TextComponent(event.getMessage());
        FullPlayer sender = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        IChannelTarget target = null;

        event.setCanceled(true);

        switch (sender.getChatTarget()) {
            case GENERAL -> { WarOfSquirrels.instance.getBroadCastHandler().getWorldChannel().SendMessage(sender, message.copy()); return; }
            case CITY -> target = sender.getCity();
            case FACTION -> target = sender.getCity().getFaction();
            case WAR -> target = WarOfSquirrels.instance.getWarHandler().getWar(sender);
            case PARTY -> target = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(sender);
        }

        if (target == null) return;

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(target, sender, message.copy(), false);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        if (!player.getLastDimensionKey().equals(Level.OVERWORLD)) return;

        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(Utils.WorldToChunk(event.getPlayer().getBlockX(), event.getPlayer().getBlockZ()));

        if (territory == null) return;

        if (WarOfSquirrels.instance.getWarHandler().getWar(territory) == null) return;

        float value = event.getOriginalSpeed();
        value += value * territory.getBiome().ratioBreakingSpeed();

        event.setNewSpeed(value);
    }
}
