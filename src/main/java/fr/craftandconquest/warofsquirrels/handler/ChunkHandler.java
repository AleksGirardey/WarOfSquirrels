package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
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
    private final Map<IFortification, List<Chunk>> fortificationMap;

    public static String DirName = "/WorldData";
    protected static String JsonName = "/ChunkHandler.json";

    public ChunkHandler(Logger logger) {
        super("[WoS][ChunkHandler]", logger);
        chunksMap = new HashMap<>();
        fortificationMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<>() {
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
        ChunkLocation chunkLocation = new ChunkLocation(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());

        dataArray.remove(chunk);
        chunksMap.entrySet().removeIf(entry -> entry.getKey().equals(chunkLocation));
        fortificationMap.get(chunk.getFortification()).remove(chunk);
        return true;
    }

    private void LogChunkCreation(Chunk chunk) {
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(chunk.getRelatedCity(), null, chunk.creationLogText(), true);
        Logger.info(PrefixLogger + " Chunk created at " + chunk);
    }

    public boolean add(Chunk chunk) {
        ChunkLocation position = new ChunkLocation(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());

        if (!chunksMap.containsKey(position)) {
            chunksMap.put(position, chunk);
        }

        if (chunk.getFortification() != null) {
            if (!fortificationMap.containsKey(chunk.getFortification())) {
                fortificationMap.put(chunk.getFortification(), new ArrayList<>());
            }

            if (!fortificationMap.get(chunk.getFortification()).contains(chunk))
                fortificationMap.get(chunk.getFortification()).add(chunk);
        }

        if (!dataArray.contains(chunk)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<>();
            dataArray.add(chunk);
        }

        return true;
    }

    public boolean deleteCity(City city) {
        if (fortificationMap.containsKey(city)) {
            for (Chunk chunk : fortificationMap.get(city)) {
                ChunkLocation chunkLocation = new ChunkLocation(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());
                dataArray.remove(chunk);
                chunksMap.keySet().removeIf(k -> k.equals(chunkLocation));
            }
            fortificationMap.keySet().removeIf(fortification -> fortification.equals(city));
        }

        Save();
        return true;
    }

    public boolean deleteBastion(Bastion bastion) {
        if (fortificationMap.containsKey(bastion)) {
            for (Chunk chunk : fortificationMap.get(bastion)) {
                ChunkLocation chunkLocation = new ChunkLocation(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());
                dataArray.remove(chunk);
                chunksMap.keySet().removeIf(k -> k.equals(chunkLocation));
            }
            fortificationMap.forEach((k, v) -> WarOfSquirrels.instance.debugLog("Fmap on delete : " + k + " - " + v.size()));

            fortificationMap.entrySet().removeIf(entry -> entry.getKey().equals(bastion));
        }

        Save();
        return true;
    }

    public boolean canPlaceOutpost(City city, ChunkLocation location) {
        return Utils.CanPlaceOutpost(location.PosX, location.PosZ);
    }

    public boolean canPlaceChunk(Territory territory, City city, ChunkLocation location) {
        Chunk bot = getChunk(location.PosX, location.PosZ - 1, location.DimensionId);
        Chunk top = getChunk(location.PosX, location.PosZ + 1, location.DimensionId);
        Chunk left = getChunk(location.PosX - 1, location.PosZ, location.DimensionId);
        Chunk right = getChunk(location.PosX + 1, location.PosZ, location.DimensionId);

        Vector2 territoryPos = new Vector2(territory.getPosX(), territory.getPosZ());
        Vector2 botTerritoryPos = null;

        boolean botNotNull = bot != null;
        boolean botSameCity = botNotNull && bot.getRelatedCity().equals(city);
        if (botNotNull)
            botTerritoryPos = Utils.FromChunkToTerritory(bot.getPosX(), bot.getPosZ());
        boolean botSameTerritory = botNotNull && botTerritoryPos.equals(territoryPos);

        boolean botCorrect = botSameCity && botSameTerritory;
        boolean topCorrect = top != null && top.getRelatedCity().equals(city) && Utils.FromChunkToTerritory(top.getPosX(), top.getPosZ()).equals(territoryPos);
        boolean rightCorrect = right != null && right.getRelatedCity().equals(city) && Utils.FromChunkToTerritory(right.getPosX(), right.getPosZ()).equals(territoryPos);
        boolean leftCorrect = left != null && left.getRelatedCity().equals(city) && Utils.FromChunkToTerritory(left.getPosX(), left.getPosZ()).equals(territoryPos);

        return botCorrect || topCorrect || rightCorrect || leftCorrect;
    }

    public List<Chunk> getHomeBlockList() {
        return chunksMap.values().stream().filter(Chunk::getHomeBlock).collect(Collectors.toList());
    }

    public List<Chunk> getOutpostList(City city) {
        if (fortificationMap.containsKey(city))
            return fortificationMap.get(city).stream().filter(Chunk::getOutpost).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public int getSize(IFortification fortification) {
        int chunksValue = 0;
        int outpostValue = 0;

        if (fortificationMap.containsKey(fortification))
            chunksValue = fortificationMap.get(fortification).size();
        if (fortification.getFortificationType() == IFortification.FortificationType.CITY)
            outpostValue = getOutpostSize((City) fortification);

        return chunksValue - outpostValue;
    }

    public int getOutpostSize(City city) {
        return getOutpostList(city).size();
    }

    public Chunk getHomeBlock(IFortification fortification) {
        if (fortificationMap.get(fortification) == null) return null;

        for (Chunk chunk : fortificationMap.get(fortification)) {
            if (chunk.getHomeBlock()) return chunk;
        }

        return null;
    }

    public boolean setHomeBlock(FullPlayer player) {
        Vector3 position = player.lastPosition;

        Chunk oldHB, newHB;

        newHB = getChunk((int) position.x, (int) position.z, player.getLastDimensionKey());
        oldHB = getHomeBlock(newHB.getFortification());

        newHB.setHomeBlock(true);
        if (oldHB != null)
            oldHB.setHomeBlock(false);

        newHB.setRespawnPoint(position);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newHB.getRelatedCity(), null,
                new TextComponent(String.format("Fortification '%s' homeblock is now set to [%d;%d;%d]",
                        newHB.getFortification().getDisplayName(),
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
        ChunkPos chunkPos = Utils.FromWorldToChunkPos((int) position.x, (int) position.z);
        return getChunk(chunkPos.x, chunkPos.z, dimensionId);
    }

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
            if (chunk.getFortificationUuid().equals(city.getCityUuid()))
                list.add(chunk);
        }
        return list;
    }

    public List<Chunk> getChunks(Bastion bastion) {
        List<Chunk> list = new ArrayList<>();

        for (Chunk chunk : dataArray) {
            if (chunk.getFortificationUuid().equals(bastion.getUniqueId()))
                list.add(chunk);
        }
        return list;
    }

    public boolean exists(int posX, int posZ, ResourceKey<Level> dimension) {
        for(Chunk chunk : dataArray) {
            WarOfSquirrels.instance.debugLog(posX + " vs " + chunk.getPosX() + " - " + posZ + " vs " + chunk.getPosZ());
            if (chunk.getPosX() == posX && chunk.getPosZ() == posZ)
                return true;
        }
        return false;
    }

    public boolean exists(Chunk chunk) {
        return exists(chunk.getPosX(), chunk.getPosZ(), chunk.getDimension());
    }

    public String getListAsString() {
        StringBuilder asString = new StringBuilder();

        for (Chunk c : dataArray) {
            asString.append(c).append("\n");
        }
        return asString.toString();
    }

    public Chunk CreateChunk(int posX, int posZ, IFortification fortification, ResourceKey<Level> dimension, String name) {
        Chunk chunk = new Chunk(posX, posZ, fortification, dimension);

        chunk.setName(name);
        chunk.setFortification();

        add(chunk);

        LogChunkCreation(chunk);
        return chunk;
    }

    public Chunk CreateChunk(int posX, int posZ, IFortification fortification, ResourceKey<Level> dimension) {
        return CreateChunk(posX, posZ, fortification, dimension, String.format("Chunk[%d;%d]", posX, posZ));
    }

    public void updateDependencies() {
        for (Chunk chunk : dataArray) {
            chunk.updateDependencies();

            if (!fortificationMap.containsKey(chunk.getFortification())) {
                fortificationMap.put(chunk.getFortification(), new ArrayList<>());
            }
            if (!fortificationMap.get(chunk.getFortification()).contains(chunk))
                fortificationMap.get(chunk.getFortification()).add(chunk);
        }

        fortificationMap.forEach((k, v) -> WarOfSquirrels.instance.debugLog("After UpdateDependency: " + k + " - " + v.size()));

        Save();
    }

    public Vector3 getSpawnOnTerritory(Territory territory, City city) {
        List<Chunk> outposts = getOutpostList(city);

        for (Chunk chunk : outposts) {
            Vector2 pair = Utils.FromChunkToTerritory(chunk.getPosX(), chunk.getPosZ());

            WarOfSquirrels.instance.debugLog(territory.getPosX() + " / " + pair.x + " - " + territory.getPosZ() + " / " + pair.y);

            if (territory.getPosX() != pair.x || territory.getPosZ() != pair.y) continue;

            return chunk.getRespawnPoint();
        }
        return city.getHomeBlock().getRespawnPoint();
    }
}
