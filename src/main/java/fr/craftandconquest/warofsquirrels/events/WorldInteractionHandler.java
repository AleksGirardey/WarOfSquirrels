package fr.craftandconquest.warofsquirrels.events;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Logger;

public class WorldInteractionHandler {

    private final Logger logger;

    public WorldInteractionHandler(Logger logger){
        this.logger = logger;
    }

    @SubscribeEvent
    public void BuildEvent(BlockEvent.BreakEvent event) {}
}
