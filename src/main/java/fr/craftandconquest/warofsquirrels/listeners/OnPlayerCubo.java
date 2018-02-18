package fr.craftandconquest.warofsquirrels.listeners;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class        OnPlayerCubo {
    @Listener
    public void     onPlayerCubo(InteractBlockEvent event) {
        DBPlayer    player = Core.getPlayerHandler().get(event.getCause().first(Player.class).orElseGet(null));
        Boolean     b;

        if (player != null && Core.getCuboHandler().playerExists(player)) {
            //player.sendMessage(Text.of("[cubo Debug] My cubo mode is activated"));
            b = event instanceof InteractBlockEvent.Primary;
            Core.getCuboHandler().set(
                    player,
                    event.getTargetBlock().getLocation().get().getBlockPosition(),
                    b);
            event.setCancelled(true);
        }
    }
}
