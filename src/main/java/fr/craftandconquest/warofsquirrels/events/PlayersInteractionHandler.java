package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.WarHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.*;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

import java.util.List;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayersInteractionHandler {

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getEntity().getUUID());
        if (player != null)
            player.setLastDimension(event.getTo().location().getPath());
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerMove(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() == null || !(event.getEntity() instanceof Player playerEntity)) {
            return;
        }

        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player == null) return;

        Vector3 newPosition = new Vector3(
                Math.round(playerEntity.getBlockX()),
                Math.round(playerEntity.getBlockY()),
                Math.round(playerEntity.getBlockZ()));

        if (player.lastPosition.equals(newPosition)) return;

        Vector3 lastPosition = player.lastPosition;
        player.lastPosition = newPosition;
        ResourceKey<Level> dimensionId = playerEntity.getCommandSenderWorld().dimension();

        List<AdminCubo> cubos = WarOfSquirrels.instance.getAdminHandler().getAll(newPosition, dimensionId);

        for (AdminCubo cubo : cubos) {
            if (cubo != null)
            {
                if (cubo.isTeleport()) {
                    AdminCubo target = WarOfSquirrels.instance.getAdminHandler().get(cubo.getLinkedPortal());

                    if (target != null) {
                        SpawnTeleporter tp = new SpawnTeleporter(target.getRespawnPoint());
                        ServerLevel level = WarOfSquirrels.server.getLevel(target.getDimensionKey());

                        if (level == null) return;

                        if (cubo.isClearInventoryOnTp() && !player.isAdminMode())
                            player.getPlayerEntity().getInventory().clearContent();

                        player.getPlayerEntity().changeDimension(level, tp);
                    }
                }
            }
        }

        Chunk lastChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(player.getLastChunkX(), player.getLastChunkZ(), dimensionId);
        Chunk newChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(player.getPlayerEntity().chunkPosition().x, player.getPlayerEntity().chunkPosition().z, dimensionId);

        if (newChunk == null || lastChunk == null || lastChunk.equals(newChunk)) return;

        Territory newTerritory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(new Vector2(newChunk.getPosX(), newChunk.getPosZ()));

        if (WarOfSquirrels.instance.getWarHandler().IsConcerned(newTerritory, newChunk)) {
            War war = WarOfSquirrels.instance.getWarHandler().getWar(newChunk.getRelatedCity());

            if (!war.isDefender(player) && !war.isAttacker(player)) {
/*                SpawnTeleporter tp = new SpawnTeleporter(new Vector3(player.lastPosition.x, player.lastPosition.y, player.lastPosition.z));
                ServerLevel level = WarOfSquirrels.server.getLevel(player.getLastDimensionKey());

                if (level == null) return;*/

                player.getPlayerEntity().moveTo(new Vec3(lastPosition.x, lastPosition.y, lastPosition.z));
                player.sendMessage(ChatText.Error("You cannot walk into this battlefield."));
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

        if (dimensionId == Level.OVERWORLD) {
            Territory oldTerritory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(new Vector2(event.getOldPos().chunk().x, event.getOldPos().chunk().z));
            Territory newTerritory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(new Vector2(event.getNewPos().chunk().x, event.getNewPos().chunk().z));

            if (newTerritory == null) {
                WarOfSquirrels.instance.debugLog("Out of bounds ? [" + player.getPlayerEntity().getBlockX() + ";" + player.getPlayerEntity().getBlockY() + ";" + player.getPlayerEntity().getBlockZ() + "]");
                return;
            }

            if (oldTerritory != newTerritory) {
                player.sendMessage(ChatText.Colored("~~| " + newTerritory.getExtendedDisplayName() + " |~~", ChatFormatting.DARK_GREEN));
                WarOfSquirrels.instance.getPlayerHandler().OnTerritoryChange(player, newTerritory);
            }
        }

        Chunk lastChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(player.getLastChunkX(), player.getLastChunkZ(), dimensionId);
        Chunk newChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(event.getNewPos().chunk().x, event.getNewPos().chunk().z, dimensionId);

        String place = null;

        if (lastChunk != null) {
            if (newChunk != null) {
                if (lastChunk == newChunk) return;

                if (lastChunk.getRelatedCity() != newChunk.getRelatedCity()) {
                    place = newChunk.getFortification().getDisplayName();

                    if (newChunk.getFortification().getFortificationType() == IFortification.FortificationType.BASTION) {
                        place += (" - " + newChunk.getFortification().getRelatedCity().getDisplayName());
                    }
                }
            } else {
                place = "Wilderness";
            }
        } else {
            if (newChunk != null) {
                place = newChunk.getFortification().getDisplayName();

                if (newChunk.getFortification().getFortificationType() == IFortification.FortificationType.BASTION) {
                    place += (" - " + newChunk.getFortification().getRelatedCity().getDisplayName());
                }
            }
        }

        if (place != null)
            player.sendMessage(ChatText.Colored("~~| " + place + (newChunk != null && newChunk.getHomeBlock() ? " HomeBlock" : "") +" |~~", ChatFormatting.GOLD));
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPvPDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player target) || !(event.getSource().getEntity() instanceof Player))
            return;
        FullPlayer fullPlayerTarget = WarOfSquirrels.instance.getPlayerHandler().get(target.getUUID());

        if (event.getEntity().level == WarOfSquirrels.server.getLevel(WarOfSquirrels.SPAWN))
            event.setCanceled(true);

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

        int ratio = 0;
        int cap = 0;

        boolean isKillInWar = warHandler.Contains(fullPlayerTarget) && warHandler.Contains(fullPlayerKiller);

        if (isKillInWar) {
            WarOfSquirrels.instance.getWarHandler().AddPoints(fullPlayerKiller, fullPlayerTarget);
            ratio = 15;
            cap = 4;
        } else {
            Utils.IncrementKillCount(fullPlayerTarget, fullPlayerKiller);
            ratio = 10;
            cap = 3;
        }

        int scoreTarget = Math.max(fullPlayerTarget.getScore().getGlobalScore(), 1);
        int scoreKiller = Math.max(fullPlayerKiller.getScore().getGlobalScore(), 1);

        if (scoreKiller >= scoreTarget) return;

        boolean targetHasCity = fullPlayerTarget.getCity() != null;
        boolean targetHasFaction = targetHasCity && fullPlayerTarget.getCity().getFaction() != null;
        boolean killerHasCity = fullPlayerKiller.getCity() != null;
        boolean killerHasFaction = killerHasCity && fullPlayerKiller.getCity().getFaction() != null;

        boolean areEnemies = targetHasFaction && killerHasFaction && WarOfSquirrels.instance.getDiplomacyHandler().getEnemies(
                fullPlayerKiller.getCity().getFaction()).contains(fullPlayerTarget.getCity().getFaction());

        if (areEnemies) {
            int score = ratio * Math.min(cap, (scoreTarget / scoreKiller));

            fullPlayerKiller.getScore().AddScore(score);
            fullPlayerKiller.sendMessage(ChatText.Success("You won '" + score + "' score by killing '" + fullPlayerTarget.getDisplayName() + "'"));
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnChatEvent(ServerChatEvent event) {
        if (event.getPlayer() == null) return;

        Component message = event.getMessage();
        FullPlayer sender = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer().getUUID());

        IChannelTarget target = null;

        event.setCanceled(true);

        BroadCastTarget chatTarget = sender.getChatTarget();

        switch (chatTarget) {
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
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(event.getEntity().getUUID());

        if (!player.getLastDimensionKey().equals(Level.OVERWORLD)) return;

        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(Utils.FromWorldToChunk(event.getEntity().getBlockX(), event.getEntity().getBlockZ()));

        if (territory == null) return;

        if (WarOfSquirrels.instance.getWarHandler().getWar(territory) == null) return;

        float value = event.getOriginalSpeed();
        value -= value * territory.getBiome().ratioBreakingSpeed();

        event.setNewSpeed(value);
    }
}
