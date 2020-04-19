package fr.craftandconquest.warofsquirrels.utils;

import com.sun.javafx.geom.Vec2d;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;

import java.util.List;

public class Utils {

    public static boolean       CanPlaceOutpost(int posX, int posZ) {
        int                     value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceOutpost());
    }

    public static int       NearestHomeBlock(int posX, int posZ) {
        double              closerDistance = WarOfSquirrels.instance.getConfig().getDistanceCities();
        Vec2d playerChunk = new Vec2d(posX, posZ);
        List<Chunk> homeBlockList;

        homeBlockList = WarOfSquirrels.instance.getChunkHandler().getHomeBlockList();
        if (homeBlockList.size() == 0)
            return (-1);
        for (Chunk c : homeBlockList) {
            Vec2d   vec = new Vec2d(c.posX, c.posZ);
            double  dist = vec.distance(playerChunk);

            closerDistance = Double.min(dist, closerDistance);
        }
        return (int) closerDistance;
    }
}
