package fr.craftandconquest.warofsquirrels.utils;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.*;

public class Utils {
    private static final int offset = 64;
    private static final int chunkOffset = 4;

    public static Vector2 FromWorldToChunk(int posX, int posZ) {
        ChunkPos pos = FromWorldToChunkPos(posX, posZ);

        return new Vector2(pos.x, pos.z);
    }

    public static ChunkPos FromWorldToChunkPos(int posX, int posZ) {
        return new ChunkPos(Math.floorDiv(posX, 16), Math.floorDiv(posZ, 16));
    }

    public static Vector2 FromWorldToTerritory(int posX, int posZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize();

        return new Vector2(Math.floorDiv(posX + offset, size), Math.floorDiv(posZ + offset, size));
    }

    public static Vector2 FromChunkToTerritory(int chunkX, int chunkZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize() / 16;

        return new Vector2(Math.floorDiv(chunkX + chunkOffset, size), Math.floorDiv(chunkZ + chunkOffset, size));
    }

    public static Vector3 FromTerritoryToWorld(int territoryX, int territoryZ) {
        int size = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int half = size / 2;

        int posX = territoryX * size + half;
        int posZ = territoryZ * size + half;

        return new Vector3(posX - offset, 100, posZ - offset);
    }

    public static boolean CanPlaceOutpost(int posX, int posZ) {
        int value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceOutpost());
    }

    public static boolean CanPlaceCity(int posX, int posZ) {
        int value = NearestHomeBlock(posX, posZ);

        return (value == -1 || value >= WarOfSquirrels.instance.getConfig().getDistanceCities());
    }

    public static boolean CanPlaceGuild(int posX, int posZ, ResourceKey<Level> dimension) {
        ChunkHandler handler = WarOfSquirrels.instance.getChunkHandler();

        Chunk chunkTop = handler.getChunk(posX, posZ + 1, dimension);
        Chunk chunkBot = handler.getChunk(posX, posZ - 1, dimension);
        Chunk chunkLeft = handler.getChunk(posX - 1, posZ, dimension);
        Chunk chunkRight = handler.getChunk(posX + 1, posZ, dimension);

        return chunkTop == null && chunkBot == null && chunkLeft == null && chunkRight == null;
    }

    @Nullable
    public static ReSpawnPoint NearestSpawnPoint(Player playerEntity) {
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (player.getCity() == null) return null;

        Vec3 spawnPoint;
        Vector3 spawn;

        if (player.isInWar()) {
            War war = WarOfSquirrels.instance.getWarHandler().getWar(player);

            if (war.isAttacker(player))
                spawn = war.getAttackerSpawn(player);
            else
                spawn = war.getDefenderSpawn(player);
        } else
            spawn = player.getCity().getSpawn();

        spawnPoint = new Vec3(spawn.x, spawn.y, spawn.z);

        return new ReSpawnPoint(Level.OVERWORLD, new BlockPos(spawnPoint));
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
            CityRank rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(city.getCityUpgrade().getLevel().getCurrentLevel());

            if (city.getOwner() == player)
                return String.format("%s %s", rank.getPrefixMayor(), player.getDisplayName());
            else if (player.getAssistant())
                return String.format("%s %s", rank.getPrefixAssistant(), player.getDisplayName());
        }
        return player.getDisplayName();
    }

    public static String getPlayTime(long value) {
        int time = (int) value;
        int hoursInMillis = 60 * 60 * 1000;
        int minInMillis = 60 * 1000;
        int secInMillis = 1000;

        int hours = time / hoursInMillis;
        time = time % hoursInMillis;

        int min = time / minInMillis;
        time = time % minInMillis;

        int sec = time / secInMillis;

        return (hours > 0 ? hours + "h " : "") + (min > 0 ? min + "min " : (hours > 0 ? "00min " : "")) + sec + "sec";
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
            message.append(freeCityList.get(i).getDisplayName() + "");
            if (i != freeCityList.size() - 1)
                message.append(", ");
        }

        return message;
    }

    public static MutableComponent SplitToStack(int amount) {
        MutableComponent message = MutableComponent.create(ComponentContents.EMPTY);
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

    public static void displayInfoFeather(Player player, BlockPos pos, ResourceKey<Level> dimensionId) {
//        Vector2 territoryPos = Utils.WorldToTerritoryCoordinates(pos.getX(), pos.getZ());
        int posX = pos.getX();
        int posZ = pos.getZ();
        ChunkPos chunkPos = Utils.FromWorldToChunkPos(posX, posZ);
        Vector2 chunkVector = new Vector2(chunkPos.x, chunkPos.z);
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().getFromChunkPos(chunkVector);

        if (territory == null) return;

        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(chunkPos.x, chunkPos.z, dimensionId);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(new Vector3(posX, pos.getY(), posZ));

        displayInfoFeather(player, territory, chunkPos, chunk, cubo);
    }

    public static void displayInfoFeather(Player player, Territory territory, ChunkPos chunkPos, Chunk chunk,  Cubo cubo) {
        MutableComponent message = ChatText.Colored("", ChatFormatting.LIGHT_PURPLE);

        if (territory != null) {
            message.append(MessageFormat.format("==| Territory {0} [{1};{2}] |==\n  Owner : {3}\n",
                    territory.getExtendedDisplayName(), territory.getPosX(), territory.getPosZ(),
                    (territory.getFaction() == null ? "None" : (territory.getFaction().getDisplayName() + (territory.isHasFallen() ? " (Has fallen)" : "")))));
            message.append("  Biome(s):\n");
            message.append(territory.getBiome().asComponent());
        }

        if (chunkPos != null) {
            message.append(MessageFormat.format("==| Chunk [{0};{1}]" + (chunk != null && chunk.getHomeBlock() ? "[HB]" : "") +" |==\n  Owner : {2}\n",
                    chunkPos.x, chunkPos.z,
                    (chunk == null ? "None" : chunk.getFortification().getDisplayName())));
        }

        List<Influence> influenceList  = WarOfSquirrels.instance.getInfluenceHandler().getAll(territory);
        if (influenceList.size() > 0) {
            message.append("==| Influence |==\n");

            for (Influence influence : influenceList) {
                if (influence.getFaction() != null)
                    message.append("  [Faction] ").append(influence.getFaction().getDisplayName());
                else
                    message.append("  [City] ").append(influence.getCity().getDisplayName());

                message.append(" [").append(influence.getValue() + "").append("/").append(influence.getTerritory().getInfluenceMax() + "").append("]\n");
            }
        }

        if (cubo != null) {
            message.append("==| Cubo '" + cubo.getDisplayName() + "' [" + cubo.getOwner().getDisplayName() + "] |==");
        }

        player.sendSystemMessage(message);
    }

    public static void IncrementKillCount(FullPlayer target, FullPlayer killer) {
        if (target.getCity() != null && killer.getCity() != null) {
            if (target.getCity().equals(killer.getCity()) ||
                    (target.getCity().getFaction() != null
                            && killer.getCity().getFaction() != null
                            && target.getCity().getFaction().equals(killer.getCity().getFaction())))
                return;
        }

        killer.setFreeForAllPlayerKillCount(killer.getFreeForAllPlayerKillCount() + 1);
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
