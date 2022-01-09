package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ReSpawnPoint;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerSetSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RespawnEvents {
    @SubscribeEvent
    public void OnDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        SetPlayerRespawnPoint(player);
    }

    @SubscribeEvent
    public void OnJoinWorld(EntityJoinWorldEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !event.getEntity().isAlive()) return;

        SetPlayerRespawnPoint(player);
    }

    @SubscribeEvent
    public void OnSetSpawn(PlayerSetSpawnEvent event) {
        if (event.isForced()) return;
    }

    private void SetPlayerRespawnPoint(ServerPlayer player) {
        FullPlayer fullPlayer = WarOfSquirrels.instance.getPlayerHandler().get(player.getUUID());

        if (fullPlayer == null || fullPlayer.getCity() == null) return;

        ReSpawnPoint spawnPoint = Utils.NearestSpawnPoint(player);
        ServerPlayer serverPlayer = WarOfSquirrels.server.getPlayerList().getPlayer(player.getUUID());

        if (serverPlayer != null && spawnPoint != null) {
            serverPlayer.setRespawnPosition(spawnPoint.dimension, spawnPoint.position, 0f, true, false);
        }
    }
}
