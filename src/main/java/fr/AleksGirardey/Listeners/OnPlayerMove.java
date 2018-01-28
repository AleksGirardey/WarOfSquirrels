package fr.AleksGirardey.Listeners;

import com.flowpowered.math.vector.Vector3d;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class OnPlayerMove {
    @Listener
    public void onPlayerMove(MoveEntityEvent event, @First Player player) {
        if (!(event.getTargetEntity() instanceof Player))
            return;
        DBPlayer            p = Core.getPlayerHandler().get(player);
        World               world = player.getWorld();

        int                 x = event.getFromTransform().getLocation().getBlockX(),
                z = event.getFromTransform().getLocation().getBlockZ(),
                lastX, lastZ;



        Chunk warChunk = Core.getChunkHandler().get(x / 16, z / 16, world);
        if (warChunk != null) {
            War war = Core.getWarHandler().getWar(warChunk.getCity());
            if (war != null && war.getDefender() == warChunk.getCity()) {
                if (!war.contains(p) && !(event instanceof MoveEntityEvent.Teleport)) {
                    Vector3d position = new Vector3d(p.getLastChunkX() * 16, p.getPosY(), p.getLastChunkZ() * 16);
                    event.setToTransform(event.getToTransform().setPosition(position));
                }
            }
        }

        if (x / 16 == p.getLastChunkX()
                && z / 16 == p.getLastChunkZ()
                && player.getWorld() == p.getLastWorld()) {
            //Core.getLogger().warn("Same lastChunk [" + p.getLastChunkX() + ";" + p.getLastChunkZ() + "] vs [" + (x/16) + ";" + (z/16) + "]");
            return;
        }
        else {
            lastX = p.getLastChunkX();
            lastZ = p.getLastChunkZ();
            p.setLastChunkX(x / 16);
            p.setLastChunkZ(z / 16);
            p.setLastWorld(player.getWorld());
        }
        Chunk     lastC, C;

        lastC = Core.getChunkHandler().get(lastX, lastZ, world);
        C = Core.getChunkHandler().get(p.getLastChunkX(), p.getLastChunkZ(), world);

        /* Il faut identifier si le changement de chunk indique un changement de propriété */

        if (lastC != null) {
            if (C == null)
                p.sendMessage(Text.of("~~ Wilderness ~~"));
            else if (C.getCity() != lastC.getCity())
                p.sendMessage(Text.of("~~ " + Core.getInfoCityMap().get(C.getCity()).getCityRank().getName()
                        + " " + C.getCity().getDisplayName() + " ~~"));
        } else {
            if (C != null)
                p.sendMessage(Text.of("~~ " + Core.getInfoCityMap().get(C.getCity()).getCityRank().getName()
                        + " " + C.getCity().getDisplayName() + " ~~"));
        }
    }
}
