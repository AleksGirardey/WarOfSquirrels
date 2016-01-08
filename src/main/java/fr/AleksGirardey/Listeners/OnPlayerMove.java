package fr.AleksGirardey.Listeners;

import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class OnPlayerMove {

    @Listener
    public void onPlayerMove(DisplaceEntityEvent.Move.TargetPlayer event) {
        ChunkHandler        ch = Core.getChunkHandler();
        Player              player = event.getTargetEntity();
        int                 id1, id2;
        boolean             c1, c2;
        Chunk               chunk1 = new Chunk(player), chunk2 = new Chunk(player);

        try {
            chunk1.setX(event.getFromTransform().getLocation().getBlockX() / 16);
            chunk1.setZ(event.getFromTransform().getLocation().getBlockZ() / 16);
            chunk2.setX(event.getToTransform().getLocation().getBlockX() / 16);
            chunk2.setZ(event.getToTransform().getLocation().getBlockZ() / 16);



            if (!chunk1.equals(chunk2)) {
                c1 = ch.exists( chunk1.getX(), chunk1.getZ());
                c2 = ch.exists( chunk2.getX(), chunk2.getZ());

                if (c1) {
                    if (c2) {
                        id1 = ch.getCity(chunk1.getX(), chunk2.getZ());
                        id2 = ch.getCity(chunk1.getX(), chunk2.getZ());
                        if (id1 != id2)
                            player.sendMessage(
                                    Text.of("~~" + Core.getCityHandler().<String>getElement(id2, "city_displayCityName")));
                    } else
                        player.sendMessage(Text.of("~~ Wilderness ~~"));
                } else {
                    if (c2) {
                        id2 = ch.getCity(chunk2.getX(), chunk2.getZ());
                        player.sendMessage(Text.of(
                                "~~ " + Core.getCityHandler().<String>getElement(id2, "city_displayName") + " ~~"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
