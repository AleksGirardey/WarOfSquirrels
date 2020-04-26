package fr.craftandconquest.warofsquirrels.utils;

import com.sun.javafx.geom.Vec2d;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import lombok.AllArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import java.net.ResponseCache;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    @AllArgsConstructor
    public class ReSpawnPoint {
        public DimensionType dimension;
        public BlockPos position;
    }

    public static boolean       CanPlaceOutpost(int posX, int posZ) {
        int                     value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceOutpost());
    }

    public static BlockPos NearestSpawnPoint(PlayerEntity playerEntity) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);

        Vec3d playerPosition = playerEntity.getPositionVector();
        Vec3d spawnPoint = null;

        List<Chunk> chunkList = new ArrayList<>(
                WarOfSquirrels.instance.getChunkHandler().getOutpostList(player.getCity()));
        // ToDo: Add Bastion ?
        chunkList.add(WarOfSquirrels.instance.getChunkHandler().getHomeBlock(player.getCity()));

        for (Chunk chunk : chunkList) {
            if (chunk.getDimensionId() == player.lastDimension.getId()) {
                Vec3d pos = new Vec3d(chunk.getRespawnX(), chunk.getRespawnY(), chunk.getRespawnZ());
                if (spawnPoint == null || (playerPosition.distanceTo(spawnPoint) > playerPosition.distanceTo(pos)))
                    spawnPoint = pos;
            }
        }

        if (spawnPoint == null) {
            Chunk homeBlock = WarOfSquirrels.instance.getChunkHandler().getHomeBlock(player.getCity());
            spawnPoint = new Vec3d(homeBlock.getRespawnX(), homeBlock.getRespawnY(), homeBlock.getRespawnZ());
        }

        return new ReSpawnPoint(dimension, new BlockPos(spawnPoint));
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
