package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;
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
        if (!Load(new TypeReference<List<Chunk>>() {})) return;

        Log();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    @Override
    public boolean Delete(Chunk chunk) {
        dataArray.remove(chunk);
        chunksMap.remove(chunk);
        cityMap.get(chunk.getCity()).remove(chunk);
        return true;
    }

    private void LogChunkCreation(Chunk chunk) {
        Logger.info(PrefixLogger + " Chunk created at " + chunk);
    }

    public boolean add(Chunk chunk) {
        if (chunksMap.containsKey(chunk)) return false;

        ChunkLocation position = new ChunkLocation(chunk.posX, chunk.posZ, chunk.getDimensionId());

        if (!dataArray.contains(chunk)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<Chunk>();
            dataArray.add(chunk);
        }
        chunksMap.put(position, chunk);
        if (!cityMap.containsKey(chunk.getCity()))
            cityMap.put(chunk.getCity(), new ArrayList<>());
        cityMap.get(chunk.getCity()).add(chunk);

        return true;
    }

    public boolean deleteCity(City city) {
        for (Chunk chunk : cityMap.get(city)) {
            dataArray.remove(chunk);
            chunksMap.remove(chunk);
        }
        cityMap.remove(city);
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

    public List<Chunk>  getHomeBlockList() {
        return chunksMap.values().stream().filter(Chunk::getHomeBlock).collect(Collectors.toList());
    }

    public List<Chunk>  getOutpostList(City city) {
        if (cityMap.containsKey(city))
            return cityMap.get(city).stream().filter(Chunk::getOutpost).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public int getSize(City city) { return (cityMap.get(city).size() - getOutpostList(city).size()); }

    public int getOutpostSize(City city) { return getOutpostList(city).size(); }

    public Chunk getHomeBlock(City city) {
        for (Chunk chunk : cityMap.get(city)) {
            if (chunk.getHomeBlock()) return chunk;
        }

        return null;
    }

    public boolean setHomeBlock(Player player) {
        Vec3d position = player.lastPosition;

        Chunk oldHB, newHB;

        newHB = getChunk((int) position.x, (int) position.z, player.lastDimension);
        oldHB = getHomeBlock(newHB.getCity());

        newHB.setHomeBlock(true);
        if (oldHB != null)
            oldHB.setHomeBlock(false);

        newHB.setRespawnX((int) position.x);
        newHB.setRespawnY((int) position.y);
        newHB.setRespawnZ((int) position.z);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(BroadCastTarget.CITY, newHB.getCity(),
                String.format("Le HomeBlock de la ville est désormais en [%d;%d;%d]",
                        (int) position.x,
                        (int) position.y,
                        (int) position.z));
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

    public Chunk getChunk(int posX, int posZ, DimensionType dimensionType) {
        return getChunk(posX, posZ, dimensionType.getId());
    }

    public Chunk getChunk(int posX, int posZ, int dimensionId) {
        return chunksMap.getOrDefault(new ChunkLocation(posX, posZ, dimensionId), null);
    }

    public List<Chunk> getChunks(City city) {
        List<Chunk> list = new ArrayList<>();

        for (Chunk chunk : dataArray) {
            if (chunk.getCityName().equals(city.displayName))
                list.add(chunk);
        }
        return list;
    }

    public boolean exists(int posX, int posZ, DimensionType dimensionType) {
        return exists(posX, posZ, dimensionType.getId());
    }

    public boolean exists(int posX, int posZ, int dimensionId) {
        return chunksMap.containsKey(new ChunkLocation(posX, posZ, dimensionId));
    }

    public boolean exists(Chunk chunk) {
        return exists(chunk.posX, chunk.posZ, chunk.getDimensionId());
    }

    public String getListAsString() {
        StringBuilder asString = new StringBuilder();

        for (Chunk c : dataArray) {
            asString.append(c).append("\n");
        }
        return asString.toString();
    }

    public Chunk CreateChunk(int posX, int posZ, City city, int dimensionId, String name) {
        Chunk chunk = CreateChunk(posX, posZ, city, dimensionId);

        chunk.setName(name);

        return chunk;
    }

    public Chunk CreateChunk(int posX, int posZ, City city, int dimensionId) {
        Chunk chunk = new Chunk(posX, posZ, city, dimensionId);
        chunk.setName(String.format("Chunk[%d;%d]", posX, posZ));

        add(chunk);

        Save(chunksMap.values());
        LogChunkCreation(chunk);
        return chunk;
    }
}
