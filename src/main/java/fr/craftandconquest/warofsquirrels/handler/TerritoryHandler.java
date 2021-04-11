package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import javax.annotation.CheckForNull;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class TerritoryHandler extends Handler<Territory> {
    private final Map<UUID, Territory> territoryMap;
    private final Map<Faction, List<Territory>> territoriesByFaction;
    private final Territory[][] territories;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/TerritoryHandler.json";

    public TerritoryHandler(Logger logger) {
        super("[WoS][TerritoryHandler]", logger);
        int sizeMap = WarOfSquirrels.instance.config.getConfiguration().getMapSize() /
                WarOfSquirrels.instance.config.getConfiguration().getTerritorySize();
        territoryMap = new HashMap<>();
        territories = new Territory[sizeMap][sizeMap];
        territoriesByFaction = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Territory>>() {})) return;

        Log();
    }

    @Override
    protected boolean Load(TypeReference<List<Territory>> typeReference) {
        String errorMessage = String.format("%s Couldn't load Json data.", PrefixLogger);
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (new BufferedReader(new FileReader(getConfigPath())).readLine() != null) {
                dataArray = mapper.readValue(configFile, typeReference);
                if (!Populate()) {
                    Logger.error(errorMessage + " (Populate)");
                    return false;
                }
            } else {
                Generate();
            }
        } catch (IOException e) {
            Logger.error(errorMessage + "'" + getConfigPath() + "' (File creation)");
            return false;
        }
        return true;
    }

    private void Generate() {
        ConfigData config = WarOfSquirrels.instance.getConfig();
        int halfSize = config.getMapSize() / 2;
        int maxX = halfSize / config.getTerritorySize();
        int maxZ = halfSize / config.getTerritorySize();
        int minX = -maxX;
        int minZ = -maxZ;

        for (int i = minX; i < maxX; ++i) { // -10 inc to 10 exc
            for (int j = minZ; j < maxZ; ++j) { // -10 inc to 10 exc
                if (CreateTerritory("Province inconnue", i, j, null, null,
                        WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getDimension().getType().getId()) == null)
                    return;
            }
        }
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    public boolean add(Territory territory) {
        if (territoryMap.containsKey(territory.getUuid())) return false;

        if (!dataArray.contains(territory)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<>();
            dataArray.add(territory);
        }

        int offset = (WarOfSquirrels.instance.getConfig().getMapSize() / 2) / WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posX = territory.getPosX() + offset;
        int posZ = territory.getPosZ() + offset;

        territoryMap.put(territory.getUuid(), territory);
        territories[posX][posZ] = territory;
        if (!territoriesByFaction.containsKey(territory.getFaction()))
            territoriesByFaction.put(territory.getFaction(), new ArrayList<>());
        territoriesByFaction.get(territory.getFaction()).add(territory);

        return true;
    }

    public Territory CreateTerritory(String territoryName, int posX, int posZ,
                                     Faction faction, IFortification fortification, int dimensionId) {
        Territory territory = new Territory(territoryName, posX, posZ, faction, fortification, dimensionId);

        if (!add(territory))
            return null;

//        int chunkPerTerritory = (WarOfSquirrels.instance.getConfig().getTerritorySize() / 16);
//        int chunkXOffset = posX * chunkPerTerritory;
//        int chunkZOffset = posZ * chunkPerTerritory;
//
//        for (int chunkX = chunkXOffset; chunkX < chunkXOffset + chunkPerTerritory; ++chunkX) {
//            for (int chunkZ = chunkZOffset; chunkZ < chunkZOffset + chunkPerTerritory; ++chunkZ) {
//                if (chunkX == 0 && chunkZ == 0) {
//                    BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getChunk(chunkX, chunkZ).func_225549_i_();
//                    if (biomes != null) {
//                        for (int biome : biomes.func_227055_a_()) { }
//                    }
//                }
//            }
//        }

        Save(territoryMap.values());
        LogTerritoryCreation(territory);

        return territory;
    }

    @Override
    public boolean Delete(Territory territory) {
        territoryMap.remove(territory.getUuid());

        int offset = (WarOfSquirrels.instance.getConfig().getMapSize() / 2) / WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posX = territory.getPosX() + offset;
        int posZ = territory.getPosZ() + offset;

        territories[posX][posZ] = null;

        Save(territoryMap.values());

        return true;
    }

    public void LogTerritoryCreation(Territory territory) {
        Logger.info(PrefixLogger + territory + " created");
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Territories generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    public String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    @Override
    protected String getConfigPath() {
        return getConfigDir() + JsonName;
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {
        // Nothing To Do
    }

    public List<Territory> getNeighbors(Territory territory) {
        List<Territory> neighbors = new ArrayList<>();
        int halfSize = WarOfSquirrels.instance.getConfig().getMapSize() / 2;
        int max = halfSize / WarOfSquirrels.instance.getConfig().getTerritorySize();
        --max;
        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int posXMore = Math.min(posX + 1, max);
        int posXLess = Math.max(posX - 1, -max);
        int posZMore = Math.min(posZ + 1, max);
        int posZLess = Math.max(posZ - 1, -max);

        int dimensionId = territory.getDimensionId();

        if (get(posX, posZMore, dimensionId) != territory)
            neighbors.add(get(posX, posZMore, dimensionId));
        if (get(posX, posZLess, dimensionId) != territory)
            neighbors.add(get(posX, posZLess, dimensionId));
        if (get(posXMore, posZ, dimensionId) != territory)
            neighbors.add(get(posXMore, posZ, dimensionId));
        if (get(posXLess, posZ, dimensionId) != territory)
            neighbors.add(get(posXLess, posZ, dimensionId));

        return neighbors;
    }

    public Territory get(UUID uuid) { return territoryMap.get(uuid); }

    public Territory get(Vector2 chunkPos, int dimensionId) {
        Pair<Integer, Integer> pos = Utils.ChunkToTerritoryCoordinates((int) chunkPos.x, (int) chunkPos.y);

        return get(pos.getKey(), pos.getValue(), dimensionId);
    }

    @CheckForNull
    public Territory get(int posX, int posZ, int dimensionId) {
        int halfSize = WarOfSquirrels.instance.getConfig().getMapSize() / 2;
        int size = halfSize / WarOfSquirrels.instance.getConfig().getTerritorySize();
        if (posX < -size || posX >= size || posZ < -size || posZ >= size) return null;

        posX = posX + size;
        posZ = posZ + size;

        Territory territory = territories[posX][posZ];

        return territory.getDimensionId() == dimensionId ? territory : null;
    }

    public boolean claim(int posX, int posZ, Faction faction, IFortification fortification, int dimensionId) {
        Territory territory = get(posX, posZ, dimensionId);

        if (territory == null) return false;

        territory.SetFaction(faction);
        territory.SetFortification(fortification);

        return true;
    }

    public void Delete(Faction faction) {
        for (Territory territory : territoriesByFaction.get(faction)) {
            territory.SetFaction(null);
        }
        Save();
    }

    public void update() {
        for(Territory territory : dataArray)
            territory.SpreadInfluence();
    }
}
