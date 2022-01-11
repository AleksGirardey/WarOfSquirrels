package fr.craftandconquest.warofsquirrels.utils;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.*;

public class Utils {

    public static Pair<Integer, Integer> WorldToChunkCoordinates(int posX, int posZ) {
        return new Pair<>(Math.floorDiv(posX, 16), Math.floorDiv(posZ, 16));
    }

    public static ChunkPos WorldToChunkPos(int posX, int posZ) {
        return new ChunkPos(Math.floorDiv(posX, 16), Math.floorDiv(posZ, 16));
    }

    public static Vector2 ChunkToTerritoryCoordinatesVector(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize() / 16;
        return new Vector2(Math.floorDiv(posX, size), Math.floorDiv(posZ, size));
    }

    public static Pair<Integer, Integer> ChunkToTerritoryCoordinates(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize() / 16;
        return new Pair<>(Math.floorDiv(posX, size), Math.floorDiv(posZ, size));
    }

    public static Vector2 WorldToTerritoryCoordinates(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize();
        return new Vector2(Math.floorDiv(posX, size), Math.floorDiv(posZ, size));
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
        ResourceKey<Level> dimension = player.getLastDimensionKey();

        if (player.getCity() == null) return null;

        Vec3 spawnPoint = null;
        Vector3 spawn;

        if (WarOfSquirrels.instance.getWarHandler().Contains(player)) {
            War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

            if (war.isAttacker(player))
                spawn = war.getAttackerSpawn(player);
            else
                spawn = war.getDefenderSpawn(player);
        } else
            spawn = player.getCity().getSpawn();

        spawnPoint = new Vec3(spawn.x, spawn.y, spawn.z);

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
            Vector2 vec = new Vector2(c.getPosX(), c.getPosZ());
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

    public static MutableComponent getSortedPlayerList() {
        Map<City, Integer> cityMap = new LinkedHashMap<>();
        List<City> cities = WarOfSquirrels.instance.getCityHandler().getAll();

        cities.forEach(c -> {
            int size = c.getOnlinePlayers().size();

            if (size > 0)
                cityMap.put(c, size);
        });

        MutableComponent message = ChatText.Success("");

        cityMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> {
                    if (entry.getValue() <= 0) return;

                    message.append(MessageFormat.format("[{0}][{1}] {2}\n", entry.getValue(), entry.getKey().getDisplayName(), getStringFromPlayerList(entry.getKey().getOnlinePlayers())));
                });

        return message;
    }

    public static MutableComponent getSortedCityList() {
        Map<Faction, List<Pair<City, Integer>>> factions = new HashMap<>();
        Map<Faction, Integer> factionMap = new LinkedHashMap<>();
        List<City> freeCityList = new ArrayList<>();
        List<City> cityList = WarOfSquirrels.instance.getCityHandler().getAll();

        for (City city : cityList) {
            Faction faction = city.getFaction();

            if (faction != null) {
                if (!factions.containsKey(faction)) {
                    factions.put(faction, new ArrayList<>());
                    factionMap.put(faction, 0);
                }

                int size = city.getOnlinePlayers().size();

                factions.get(faction).add(new Pair<>(city, size));
                factionMap.computeIfPresent(faction, (k, v) -> v + size);
            } else {
                freeCityList.add(city);
            }
        }

        MutableComponent message = ChatText.Success("");

        factionMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(k -> {
                    message.append(MessageFormat.format("== {0} [{1}] ==\n", k.getKey().getDisplayName(), k.getValue()));
                    List<Pair<City, Integer>> subList = factions.get(k.getKey());
                    for (Pair<City, Integer> pair : subList) {
                        message.append(MessageFormat.format("  - {0}[{1}]\n",pair.getKey().getDisplayName(), pair.getValue()));
                    }
                });

        message.append(ChatText.Success("=== Free cities [" + freeCityList.size() + "] ===\n"));

        for (int i = 0; i < freeCityList.size(); ++i) {
            message.append(freeCityList.get(i).displayName + "");
            if (i != freeCityList.size() - 1)
                message.append("");
        }

        return message;
    }

    public static MutableComponent SplitToStack(int amount) {
        MutableComponent message = new TextComponent("");
        int stacks = amount / 64;
        int blocks = amount % 64;

        if (stacks > 0) {
            message.append(stacks + " stacks");
            if (blocks > 0)
                message.append(" and ");
        }
        if (blocks > 0)
            message.append(blocks + " blocks");

        return message;
    }

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
