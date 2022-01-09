package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ChunkHandler extends Handler<Chunk> {
    private final Map<ChunkLocation, Chunk> chunksMap;
    private final Map<City, List<Chunk>> cityMap;

    public static String DirName = "/WorldData";
    protected static String JsonName = "/ChunkHandler.json";

    public ChunkHandler(Logger logger) {
        super("[WoS][ChunkHandler]", logger);
        chunksMap = new HashMap<>();
        cityMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Chunk>>() {
        })) return;

        Log();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    @Override
    public boolean Delete(Chunk chunk) {
        ChunkLocation chunkLocation = new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimension());

        dataArray.remove(chunk);
        chunksMap.entrySet().removeIf(entry -> entry.getKey().equals(chunkLocation));
        cityMap.get(chunk.getCity()).remove(chunk);
        return true;
    }

    private void LogChunkCreation(Chunk chunk) {
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(chunk.getCity(), null, chunk.creationLogText(), true);
        Logger.info(PrefixLogger + " Chunk created at " + chunk);
    }

    public boolean add(Chunk chunk) {
        ChunkLocation position = new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimension());

        if (!chunksMap.containsKey(position)) {
            chunksMap.put(position, chunk);
        }

        if (!cityMap.containsKey(chunk.getCity())) {
            cityMap.put(chunk.getCity(), new ArrayList<>());
        }

        cityMap.get(chunk.getCity()).add(chunk);

        if (!dataArray.contains(chunk)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<>();
            dataArray.add(chunk);
        }

        return true;
    }

    public boolean deleteCity(City city) {

        for (Chunk chunk : cityMap.get(city)) {
            ChunkLocation chunkLocation = new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimension());
            dataArray.remove(chunk);
            chunksMap.keySet().removeIf(k -> k.equals(chunkLocation));
        }
        cityMap.keySet().removeIf(c -> c.equals(city));

        Save();
        return true;
    }

    public boolean canBePlaced(City city, boolean outpost, ChunkLocation location) {
        if (outpost) return Utils.CanPlaceOutpost(location.PosX, location.PosZ);

        Chunk bot = getChunk(location.PosX, location.PosZ - 1, location.DimensionId);
        Chunk top = getChunk(location.PosX, location.PosZ + 1, location.DimensionId);
        Chunk left = getChunk(location.PosX - 1, location.PosZ, location.DimensionId);
        Chunk right = getChunk(location.PosX + 1, location.PosZ, location.DimensionId);

        return (bot != null && bot.getCity() == city)
                || (top != null && top.getCity() == city)
                || (left != null && left.getCity() == city)
                || (right != null && right.getCity() == city);
    }

    public List<Chunk> getHomeBlockList() {
        return chunksMap.values().stream().filter(Chunk::getHomeBlock).collect(Collectors.toList());
    }

    public List<Chunk> getOutpostList(City city) {
        if (cityMap.containsKey(city))
            return cityMap.get(city).stream().filter(Chunk::getOutpost).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public int getSize(City city) {
        int chunksValue = 0;
        int outpostValue = 0;


        if (cityMap.containsKey(city))
            chunksValue = cityMap.get(city).size();
        outpostValue = getOutpostSize(city);

        return chunksValue - outpostValue;
    }

    public int getOutpostSize(City city) {
        return getOutpostList(city).size();
    }

    public Chunk getHomeBlock(City city) {
        for (Chunk chunk : cityMap.get(city)) {
            if (chunk.getHomeBlock()) return chunk;
        }

        return null;
    }

    public boolean setHomeBlock(FullPlayer player) {
        Vector3 position = player.lastPosition;

        Chunk oldHB, newHB;

        newHB = getChunk((int) position.x, (int) position.z, player.getLastDimensionKey());
        oldHB = getHomeBlock(newHB.getCity());

        newHB.setHomeBlock(true);
        if (oldHB != null)
            oldHB.setHomeBlock(false);

        newHB.setRespawnX((int) position.x);
        newHB.setRespawnY((int) position.y);
        newHB.setRespawnZ((int) position.z);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newHB.getCity(), null,
                new TextComponent(String.format("Le HomeBlock de la ville est désormais en [%d;%d;%d]",
                        (int) position.x,
                        (int) position.y,
                        (int) position.z)), true);
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Chunks generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName + JsonName;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing To Do
    }

    public Chunk getChunk(Vector3 position, ResourceKey<Level> dimensionId) {
        ChunkPos chunkPos = Utils.WorldToChunkPos((int) position.x, (int) position.z);
        return getChunk(chunkPos.x, chunkPos.z, dimensionId);
    }

//    public Chunk getChunk(int posX, int posZ, DimensionType dimensionType) {
//        return getChunk(posX, posZ, dimensionType);
//    }

    public Chunk getChunk(int posX, int posZ, ResourceKey<Level> dimensionId) {
        for (Map.Entry<ChunkLocation, Chunk> entry : chunksMap.entrySet()) {
            if (entry.getKey().equals(new ChunkLocation(posX, posZ, dimensionId)))
                return entry.getValue();
        }

        return null;
//        return chunksMap.getOrDefault(new ChunkLocation(posX, posZ, dimensionId), null);
    }

    public List<Chunk> getChunks(City city) {
        List<Chunk> list = new ArrayList<>();

        for (Chunk chunk : dataArray) {
            if (chunk.getCityUuid().equals(city.getCityUuid()))
                list.add(chunk);
        }
        return list;
    }

//    public boolean exists(int posX, int posZ, DimensionType dimensionType) {
//        return exists(posX, posZ, dimensionType);
//    }

    public boolean exists(int posX, int posZ, ResourceKey<Level> dimension) {
        return chunksMap.containsKey(new ChunkLocation(posX, posZ, dimension));
    }

    public boolean exists(Chunk chunk) {
        return exists(chunk.posX, chunk.posZ, chunk.getDimension());
    }

    public String getListAsString() {
        StringBuilder asString = new StringBuilder();

        for (Chunk c : dataArray) {
            asString.append(c).append("\n");
        }
        return asString.toString();
    }

    public Chunk CreateChunk(int posX, int posZ, City city, ResourceKey<Level> dimension, String name) {
        Chunk chunk = CreateChunk(posX, posZ, city, dimension);

        chunk.setName(name);

        return chunk;
    }

    public Chunk CreateChunk(int posX, int posZ, City city, ResourceKey<Level> dimension) {
        Chunk chunk = new Chunk(posX, posZ, city, dimension);
        chunk.setName(String.format("Chunk[%d;%d]", posX, posZ));

        add(chunk);

        LogChunkCreation(chunk);
        return chunk;
    }

    public void updateDependencies() {
        for (Chunk chunk : dataArray) {
            chunk.updateDependencies();

            if (!cityMap.containsKey(chunk.getCity())) {
                cityMap.put(chunk.getCity(), new ArrayList<>());
            }
            if (!cityMap.get(chunk.getCity()).contains(chunk))
                cityMap.get(chunk.getCity()).add(chunk);
        }
        Save();
    }
}
