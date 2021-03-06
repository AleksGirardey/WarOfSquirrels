package fr.craftandconquest.warofsquirrels.utils;

import com.sun.org.apache.xml.internal.utils.IntVector;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Pair<Integer, Integer> WorldToChunkCoordinates(int posX, int posZ) {
        return Pair.of(posX / 16, posZ / 16);
    }

    public static boolean       CanPlaceOutpost(int posX, int posZ) {
        int                     value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceOutpost());
    }

    public static boolean   CanPlaceCity(int posX, int posZ) {
        int                 value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceCities());
    }

    public static ReSpawnPoint NearestSpawnPoint(PlayerEntity playerEntity) {
        Player player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity);
        DimensionType dimension = player.lastDimension;

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
            dimension = DimensionType.getById(homeBlock.getDimensionId());
        }

        return new ReSpawnPoint(dimension, new BlockPos(spawnPoint));
    }

    public static int       NearestHomeBlock(int posX, int posZ) {
        double              closerDistance = WarOfSquirrels.instance.getConfig().getDistanceCities();
        Vector2 playerChunk = new Vector2(posX, posZ);
        List<Chunk> homeBlockList;

        homeBlockList = WarOfSquirrels.instance.getChunkHandler().getHomeBlockList();
        if (homeBlockList.size() == 0)
            return (-1);
        for (Chunk c : homeBlockList) {
            Vector2 vec = new Vector2(c.posX, c.posZ);
            double  dist = vec.distance(playerChunk);

            closerDistance = Double.min(dist, closerDistance);
        }
        return (int) closerDistance;
    }

    public static String getDisplayNameWithRank(Player player) {
        City city = player.getCity();

        if (city != null) {
            CityRank rank = city.getRank();

            if (city.getOwner() == player)
                return String.format("%s %s", rank.getPrefixMayor(), player.getDisplayName());
            else if (player.getAssistant())
                return String.format("%s %s", rank.getPrefixAssistant(), player.getDisplayName());
        }
        return player.getDisplayName();
    }

    public static String toTime(int value) {
        int                 minutes, seconds;
        String              res = "";

        minutes = value / 60;
        seconds = value % 60;
        if (minutes > 0)
            res = minutes + " min ";
        res += seconds + " s";

        return res;
    }

    public static String getStringFromPlayerList(List<Player> list) {
        StringBuilder res = new StringBuilder();
        int i = 0;

        for (Player p : list) {
            res.append(p.getDisplayName());
            if (i != list.size() - 1)
                res.append(", ");
        }
        return res.toString();
    }
}
