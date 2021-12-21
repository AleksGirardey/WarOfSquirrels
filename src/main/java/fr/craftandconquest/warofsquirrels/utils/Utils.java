package fr.craftandconquest.warofsquirrels.utils;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static Pair<Integer, Integer> WorldToChunkCoordinates(int posX, int posZ) {
        return new Pair<>(Math.floorDiv(posX, 16), Math.floorDiv(posZ, 16));
    }

    public static ChunkPos WorldToChunkPos(int posX, int posZ) {
        return new ChunkPos(Math.floorDiv(posX, 16), Math.floorDiv(posZ, 16));
    }

    public static Pair<Integer, Integer> ChunkToTerritoryCoordinates(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize() / 16;
        return new Pair<>(Math.floorDiv(posX, size), Math.floorDiv(posZ, size));
    }

    public static Pair<Integer, Integer> WorldToTerritoryCoordinates(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize();
        return new Pair<>(Math.floorDiv(posX, size), Math.floorDiv(posZ, size));
    }

    public static boolean CanPlaceOutpost(int posX, int posZ) {
        int value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceOutpost());
    }

    public static boolean CanPlaceCity(int posX, int posZ) {
        int value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceCities());
    }

    @Nullable
    public static ReSpawnPoint NearestSpawnPoint(Player playerEntity) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());
        ResourceKey<Level> dimension = player.lastDimension;

        if (player.getCity() == null) return null;

        Vec3 playerPosition = new Vec3(
                playerEntity.getOnPos().getX(),
                playerEntity.getOnPos().getY(),
                playerEntity.getOnPos().getZ());
        Vec3 spawnPoint = null;

        List<Chunk> chunkList = new ArrayList<>(WarOfSquirrels.instance.getChunkHandler().getOutpostList(player.getCity()));
        // ToDo: Add Bastion ?

        chunkList.add(WarOfSquirrels.instance.getChunkHandler().getHomeBlock(player.getCity()));

        for (Chunk chunk : chunkList) {
            if (chunk.getDimension().equals(player.lastDimension)) {
                Vec3 pos = new Vec3(chunk.getRespawnX(), chunk.getRespawnY(), chunk.getRespawnZ());
                if (spawnPoint == null || (playerPosition.distanceTo(spawnPoint) > playerPosition.distanceTo(pos))) {
                    spawnPoint = pos;
                }
            }
        }

        if (spawnPoint == null) {
            Chunk homeBlock = WarOfSquirrels.instance.getChunkHandler().getHomeBlock(player.getCity());
            spawnPoint = new Vec3(homeBlock.getRespawnX(), homeBlock.getRespawnY(), homeBlock.getRespawnZ());
            dimension = homeBlock.getDimension();
        }

        return new ReSpawnPoint(dimension, new BlockPos(spawnPoint));
    }

    public static int NearestHomeBlock(int posX, int posZ) {
        double closerDistance = WarOfSquirrels.instance.getConfig().getDistanceCities();
        Vector2 playerChunk = new Vector2(posX, posZ);
        List<Chunk> homeBlockList;

        homeBlockList = WarOfSquirrels.instance.getChunkHandler().getHomeBlockList();
        if (homeBlockList.size() == 0)
            return (-1);
        for (Chunk c : homeBlockList) {
            Vector2 vec = new Vector2(c.posX, c.posZ);
            double dist = vec.distance(playerChunk);

            closerDistance = Double.min(dist, closerDistance);
        }
        return (int) closerDistance;
    }

    public static String getDisplayNameWithRank(FullPlayer player) {
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
        int minutes, seconds;
        String res = "";

        minutes = value / 60;
        seconds = value % 60;
        if (minutes > 0)
            res = minutes + " min ";
        res += seconds + " s";

        return res;
    }

    public static String getStringFromPlayerList(List<FullPlayer> list) {
        StringBuilder res = new StringBuilder();
        int i = 0;

        for (FullPlayer p : list) {
            res.append(p.getDisplayName());
            if (i != list.size() - 1)
                res.append(", ");
        }
        return res.toString();
    }
}
