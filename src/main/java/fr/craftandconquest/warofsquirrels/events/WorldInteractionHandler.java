package fr.craftandconquest.warofsquirrels.events;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class WorldInteractionHandler {

    private final Logger logger;

    public WorldInteractionHandler(Logger logger){
        this.logger = logger;
    }

    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        List<BroadCastTarget> targets = new ArrayList<>();
        PlayerEntity playerEntity = event.getPlayer();

        PlayerHandler playerHandler = WarOfSquirrels.instance.getPlayerHandler();
        BroadCastHandler broadCastHandler = WarOfSquirrels.instance.getBroadCastHandler();

        Player player;

        if (!playerHandler.exists(playerEntity))
            player = playerHandler.CreatePlayer(playerEntity);
        else
            player = playerHandler.get(playerEntity);

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

    @SubscribeEvent
    public void PlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(event.getPlayer());

        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerToWorldAnnounce(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTargets(player);

        logger.info(String.format("[WoS][WorldInteraction][PlayerLoggedOut] Player %s removed from all channels",
                player.getDisplayName()));
    }
}
