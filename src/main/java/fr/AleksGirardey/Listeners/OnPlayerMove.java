package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.text.Text;

public class OnPlayerMove {
    @Listener
    public void onPlayerMove(MoveEntityEvent event) {
        if (!(event.getTargetEntity() instanceof Player))
            return;
        Player              pl = (Player) event.getTargetEntity();
        DBPlayer            player = Core.getPlayerHandler().get(pl);
        int                 x = event.getFromTransform().getLocation().getBlockX(),
                z = event.getFromTransform().getLocation().getBlockZ(),
                lastX, lastZ;

        if (x / 16 == player.getLastChunkX()
                && z / 16 == player.getLastChunkZ())
            return;
        else {
            lastX = player.getLastChunkX();
            lastZ = player.getLastChunkZ();
            player.setLastChunkX(x / 16);
            player.setLastChunkZ(z / 16);
        }
        Chunk     lastC, C;

        lastC = Core.getChunkHandler().get(lastX, lastZ);
        C = Core.getChunkHandler().get(player.getLastChunkX(), player.getLastChunkZ());

        /* Il faut identifier si le changement de chunk indique un changement de propriété */

        if (C != null) {
            /* Le chunk sur lequel il arrive appartient a quelqu'un */
            if (lastC == null || C.getCity() != lastC.getCity())
                /* Le Chunk d'ou il vient appartient à quelqu'un d'autre ou il n'appartient à personne */
                player.sendMessage(Text.of (
                            "~~ " + Core.getInfoCityMap().get(C.getCity()).getRank().getName()
                                    + " " + C.getCity().getDisplayName() + " ~~"));
            else
                player.sendMessage(Text.of("~~ Wilderness ~~"));
        } else {
            /* Il entre dans un chunk vierge */
            if (lastC != null)
                /* Il était dans un chunk appartenant à quelqu'un */
                player.sendMessage(Text.of("~~ Wilderness ~~"));
        }
    }
}
