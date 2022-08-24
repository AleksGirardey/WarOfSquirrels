package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ReSpawnPoint;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class RespawnEvents {
    @SubscribeEvent
    public void OnDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        SetPlayerRespawnPoint(player);
    }

    private void SetPlayerRespawnPoint(ServerPlayer player) {
        FullPlayer fullPlayer = WarOfSquirrels.instance.getPlayerHandler().get(player.getUUID());

        ReSpawnPoint spawnPoint;

        if (fullPlayer == null || fullPlayer.getCity() == null) {
            if (player.getRespawnPosition() != null)
                spawnPoint = new ReSpawnPoint(player.getRespawnDimension(), player.getRespawnPosition());
            else
                spawnPoint = ReSpawnPoint.DEFAULT_SPAWN;
        } else
            spawnPoint = Utils.NearestSpawnPoint(player);

        ServerPlayer serverPlayer = WarOfSquirrels.server.getPlayerList().getPlayer(player.getUUID());

        if (serverPlayer != null && spawnPoint != null) {
            serverPlayer.setRespawnPosition(spawnPoint.dimension, spawnPoint.position, 0f, true, false);
        }
    }
}
