package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.utils.ReSpawnPoint;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayersInteractionHandler {

    @SubscribeEvent
    public void OnLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        if (!playerHandler.exists(event.getPlayer()))
            if (playerHandler.CreatePlayer(event.getPlayer()) == null) event.setCanceled(true);
        playerHandler.get(event.getPlayer()).lastDimension = event.getPlayer().dimension;
        playerHandler.get(event.getPlayer()).lastPosition = event.getPlayer().getPositionVec();
    }

    @SubscribeEvent
    public void OnPlayerReSpawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        if (player.getCity() == null) return;

        ReSpawnPoint spawnPoint = Utils.NearestSpawnPoint(event.getPlayer());

        event.getPlayer().setSpawnPoint(spawnPoint.position, true, true, spawnPoint.dimension);
    }

    @SubscribeEvent
    public void OnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer())
                .lastDimension = event.getTo();
    }


}
