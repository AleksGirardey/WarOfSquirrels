package fr.craftandconquest.warofsquirrels.listeners;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.War;
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

        int                 xF = event.getFromTransform().getLocation().getBlockX();
        int                 yF = event.getFromTransform().getLocation().getBlockY();
        int                 zF = event.getFromTransform().getLocation().getBlockZ();
        int                 xT = event.getToTransform().getLocation().getBlockX();
        int                 yT = event.getToTransform().getLocation().getBlockY();
        int                 zT = event.getToTransform().getLocation().getBlockZ();
        int                 lastX, lastZ;
        Vector3d            safePosition;



/*        Chunk warChunk = Core.getChunkHandler().get(x / 16, z / 16, world);
        if (warChunk != null) {
            War war = Core.getWarHandler().getWar(warChunk.getCity());
            if (war != null && war.getDefender() == warChunk.getCity()) {
                if (!war.contains(p) && !(event instanceof MoveEntityEvent.Teleport) && !p.hasAdminMode()) {
                    Vector3d position = new Vector3d(p.getLastChunkX() * 16, p.getPosY(), p.getLastChunkZ() * 16);
                    event.setToTransform(event.getToTransform().setPosition(position));
                }
            }
        } */

        if (!p.hasAdminMode()) {
            Chunk warChunk = Core.getChunkHandler().get(xT / 16, zT / 16, world);
            if (warChunk != null) {
                War war = Core.getWarHandler().getWar(warChunk.getCity());
                if (war != null && war.getDefender() == warChunk.getCity()) {
                    if (!war.contains(p) && !(event instanceof MoveEntityEvent.Teleport)) {
                        Vector3d v = new Vector3d(
                                (xT > xF ? 1 : -1),
                                0,
                                (zT > zF ? 1 : -1));
                        safePosition = findSafePosition(new Vector3d(xT, yT, zT), v, world);
                        event.setToTransform(event.getToTransform().setPosition(safePosition));
                    }
                }
            }
        }

        if (xT / 16 == p.getLastChunkX()
                && zT / 16 == p.getLastChunkZ()
                && player.getWorld() == p.getLastWorld()) {
            return;
        }
        else {
            lastX = p.getLastChunkX();
            lastZ = p.getLastChunkZ();
            p.setLastChunkX(xT / 16);
            p.setLastChunkZ(zT / 16);
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

    private Vector3d    findSafePosition(Vector3d pos, Vector3d direction, World world) {
        Vector3d        newPos = pos.add(direction);
        Chunk           chunk = Core.getChunkHandler().get((int) newPos.getX() / 16, (int) newPos.getY() / 16, world);
        if (chunk != null)
            return findSafePosition(newPos, direction, world);
        return newPos;
    }
}
