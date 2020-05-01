package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class TerritoryHandler extends Handler<Territory> {
    private Map<UUID, Territory> territoryMap;
    private Map<Faction, List<Territory>> territoriesByFaction;
    private Territory[][] territories;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/TerritoryHandler.json";

    protected TerritoryHandler(Logger logger) {
        super("[WoS][TerritoryHandler]", logger);
        int sizeMap = WarOfSquirrels.instance.config.getConfiguration().getMapSize() /
                WarOfSquirrels.instance.config.getConfiguration().getTerritorySize();
        territoryMap = new HashMap<>();
        territories = new Territory[sizeMap][sizeMap];

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
        int maxX = config.getMapSize() / config.getTerritorySize();
        int maxZ = config.getMapSize() / config.getTerritorySize();

        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxZ; j++) {
                territories[i][j] = new Territory("Province inconnue", i, j, null, null,
                        WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD).getDimension().getType().getId());
            }
        }
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return false;
    }

    public boolean add(Territory territory) {
        if (territoryMap.containsKey(territory.getUuid())) return false;

        if (!dataArray.contains(territory)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<Territory>();
            dataArray.add(territory);
        }

        territoryMap.put(territory.getUuid(), territory);
        territories[territory.getPosX()][territory.getPosZ()] = territory;
        if (!territoriesByFaction.containsKey(territory.getFaction()))
            territoriesByFaction.put(territory.getFaction(), new ArrayList<>());
        territoriesByFaction.get(territory.getFaction()).add(territory);

        Save(territoryMap.values());
        LogTerritoryCreation(territory);
        return true;
    }

    public Territory CreateTerritory(String territoryName, int posX, int posZ,
                                     Faction faction, IFortification fortification, int dimensionId) {
        Territory territory = new Territory(territoryName, posX, posZ, faction, fortification, dimensionId);

        if (!add(territory))
            return null;

        return territory;
    }

    @Override
    public boolean Delete(Territory territory) {
        territoryMap.remove(territory.getUuid());
        territories[territory.getPosX()][territory.getPosZ()] = null;

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
        int max = WarOfSquirrels.instance.getConfig().getMapSize() /
                WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int posXMore = Math.min(posX + 1, max);
        int posXLess = Math.max(posX - 1, 0);
        int posZMore = Math.min(posZ + 1, max);
        int posZLess = Math.max(posZ - 1, 0);

        if (territories[posX][posZMore] != territory)
            neighbors.add(territories[posX][posZMore]);
        if (territories[posX][posZLess] != territory)
            neighbors.add(territories[posX][posZLess]);
        if (territories[posXMore][posZ] != territory)
            neighbors.add(territories[posXMore][posZ]);
        if (territories[posXLess][posZ] != territory)
            neighbors.add(territories[posXLess][posZ]);

        return neighbors;
    }

    public Territory get(UUID uuid) { return territoryMap.get(uuid); }

    public Territory get(int posX, int posZ, int dimensionId) {
        Territory territory = territories[posX][posZ];

        return territory.getDimensionId() == dimensionId ? territory : null;
    }

    public boolean claim(int posX, int posZ, Faction faction, IFortification fortification, int dimensionId) {
        Territory territory = territories[posX][posZ];

        territory.SetFaction(faction);
        territory.SetFortification(fortification);

        return true;
    }

    public void update() {
        for(Territory territory : dataArray)
            territory.SpreadInfluence();
    }
}
