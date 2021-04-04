package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.WarHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ReSpawnPoint;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayersInteractionHandler {

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        if (!playerHandler.exists(event.getPlayer()))
            if (playerHandler.CreatePlayer(event.getPlayer()) == null) event.setCanceled(true);
        playerHandler.get(event.getPlayer()).lastDimension = event.getPlayer().dimension;
        playerHandler.get(event.getPlayer()).lastPosition = new Vector3(
                (float) event.getPlayer().getPositionVec().x,
                (float) event.getPlayer().getPositionVec().y,
                (float) event.getPlayer().getPositionVec().z);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerReSpawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        if (player.getCity() == null) return;

        ReSpawnPoint spawnPoint = Utils.NearestSpawnPoint(event.getPlayer());

        event.getPlayer().setSpawnPoint(spawnPoint.position, true, true, spawnPoint.dimension);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer())
                .lastDimension = event.getTo();
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPlayerMove(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == null || !(event.getEntityLiving() instanceof PlayerEntity)) return;

        PlayerEntity playerEntity = (PlayerEntity) event.getEntityLiving();
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        Vector3 newPosition = new Vector3(
                Math.round(playerEntity.serverPosX),
                Math.round(playerEntity.serverPosY),
                Math.round(playerEntity.serverPosZ));

        int dimensionId = playerEntity.world.dimension.getType().getId();

        if (!player.lastPosition.equals(newPosition)) {
            Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(newPosition, dimensionId);
            if (chunk != null) {
                if (WarOfSquirrels.instance.getWarHandler().IsConcerned(new Vector2(newPosition.x, newPosition.z), dimensionId)) {
                    War war = WarOfSquirrels.instance.getWarHandler().getWar(chunk.getCity());

                    if (!war.isDefender(player) && !war.isAttacker(player)) {
//                        playerEntity.teleportKeepLoaded(
//                                player.lastPosition.x,
//                                player.lastPosition.y,
//                                player.lastPosition.z);

                        event.setCanceled(true);
                        return;
                    }
                }
            }

            player.lastPosition = newPosition;
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnChunkChange(EntityEvent.EnteringChunk event) {
        if (!(event.getEntity() instanceof PlayerEntity)) return;

        Player player = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getEntity());

        if (player == null) return;

        player.setLastChunkX(event.getOldChunkX());
        player.setLastChunkZ(event.getOldChunkZ());

        int dimensionId = event.getEntity().world.getDimension().getType().getId();

        Territory oldTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(new Vector2(event.getNewChunkX(), event.getNewChunkZ()),
                dimensionId);
        Territory newTerritory = WarOfSquirrels.instance.getTerritoryHandler().get(new Vector2(event.getNewChunkX(), event.getNewChunkZ()),
                dimensionId);

        if (oldTerritory != newTerritory) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("~~~~| " + newTerritory.getName() + " |~~~~")
                    .applyTextStyle(TextFormatting.GOLD));
        }

        Chunk lastChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(player.getLastChunkX(), player.getLastChunkZ(), dimensionId);
        Chunk newChunk = WarOfSquirrels.instance.getChunkHandler().getChunk(event.getNewChunkX(), event.getNewChunkZ(), dimensionId);

        if (lastChunk != null) {
            if (newChunk != null) {
                if (lastChunk != newChunk)
                    player.getPlayerEntity().sendMessage(new StringTextComponent("~~~~| " + newChunk.getCityName() + " |~~~~")
                    .applyTextStyle(TextFormatting.GOLD));
            } else {
                player.getPlayerEntity().sendMessage(new StringTextComponent("~~~~| Wilderness |~~~~")
                        .applyTextStyle(TextFormatting.GOLD));
            }
        } else {
            if (newChunk != null) {
                player.getPlayerEntity().sendMessage(new StringTextComponent("~~~~| " + newChunk.getCityName() + " |~~~~")
                        .applyTextStyle(TextFormatting.GOLD));
            }
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPvPDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) &&
                !(event.getSource().getTrueSource() instanceof PlayerEntity)) return;

        Player target = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getEntity());

        if (WarOfSquirrels.instance.getPlayerHandler().isInReincarnation(target))
            event.setAmount(10000);
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    @SubscribeEvent
    public void OnPvPDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof PlayerEntity) &&
                !(event.getSource().getTrueSource() instanceof PlayerEntity)) return;

        Player target = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getEntity());
        Player killer = WarOfSquirrels.instance.getPlayerHandler().get((PlayerEntity) event.getSource().getTrueSource());

        WarOfSquirrels.instance.getPlayerHandler().SetReincarnation(target);
        WarHandler warHandler = WarOfSquirrels.instance.getWarHandler();

        if (warHandler.Contains(target) && warHandler.Contains(killer)) {
            War war = warHandler.getWar(target);

            if (war.isAttacker(target) && war.isDefender(killer))
                war.AddDefenderKillPoints();
            else if (war.isAttacker(killer) && war.isDefender(target)) {
                if (war.isTarget(target))
                    war.AddAttackerTargetKillPoints();
                else
                    war.AddAttackerKillPoints();
            }
        }

        //ToDo: Add points leaderboard
    }
}
