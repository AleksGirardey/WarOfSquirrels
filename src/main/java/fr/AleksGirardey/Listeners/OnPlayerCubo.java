package fr.AleksGirardey.Listeners;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;

public class        OnPlayerCubo {
    @Listener
    public void     onPlayerCubo(InteractBlockEvent event) {
        DBPlayer    player = Core.getPlayerHandler().get(event.getCause().first(Player.class).orElseGet(null));
        Boolean     b;

        if (player != null && Core.getCuboHandler().playerExists(player)) {
            player.sendMessage(Text.of("[Cubo Debug] My cubo mode is activated"));
            b = event instanceof InteractBlockEvent.Primary;
            Core.getCuboHandler().set(
                    player,
                    event.getTargetBlock().getLocation().get().getBlockPosition(),
                    b);
            event.setCancelled(true);
        }
    }
}
