package fr.craftandconquest.warofsquirrels.listeners;

import fr.craftandconquest.warofsquirrels.objects.Core;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;

public class GameListener {

    @Listener
    public void onGameInit(GameInitializationEvent event) {
        Core.getUpdateHandler().create();
    }
}
