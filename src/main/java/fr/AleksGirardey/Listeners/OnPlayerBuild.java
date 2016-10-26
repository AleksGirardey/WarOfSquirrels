package fr.AleksGirardey.Listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class OnPlayerBuild {

    @Listener
    public void onPlayerBuild(ChangeBlockEvent.Place event) {
        OnPlayerDestroy.CheckBuild(event);
    }
}

