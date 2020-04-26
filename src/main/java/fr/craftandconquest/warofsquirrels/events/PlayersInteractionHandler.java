package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayersInteractionHandler {

    @SubscribeEvent
    public void OnLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        if (!playerHandler.exists(event.getPlayer()))
            playerHandler.CreatePlayer(event.getPlayer());
    }

    @SubscribeEvent
    public void OnPlayerReSpawn(PlayerEvent.PlayerRespawnEvent event) {
        event.getPlayer().setSpawnPoint(
                Utils.NearestSpawnPoint(event.getPlayer()), true, true, );
    }

    @SubscribeEvent
    public void OnDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer())
                .lastDimension = event.getTo();
    }
}
