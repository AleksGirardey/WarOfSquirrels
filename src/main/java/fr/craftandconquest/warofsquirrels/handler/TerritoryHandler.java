package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.config.ConfigData;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import org.apache.logging.log4j.Logger;

import javax.annotation.CheckForNull;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TerritoryHandler extends Handler<Territory> {
    private final static List<String> defaultTerritoryName = new ArrayList<>() {{
        add("Elyphis");
        add("Oberon");
        add("Frayja");
        add("Aelum");
        add("Dendragon");
        add("Gaia");
        add("Wardak");
        add("Anguemor");
        add("Aarvan");
        add("Vrihed");

        add("Cazan");
        add("Vocura");
        add("Glazone");
        add("Naidela");
        add("Zutrul");
        add("Oros");
        add("Iwari");
        add("Bephura");
        add("Ufone");
        add("Vagrus");

        add("Chaubone");
        add("Suwan");
        add("Epos");
        add("Datreon");
        add("Iatrai");
        add("Eunes");
        add("Aitrus");
        add("Ohira");
        add("Ison");
        add("Anor");

        add("Iqesh");
        add("Bruyela");
        add("Keiwan");
        add("Arias");
        add("Obias");
        add("Itun");
        add("Izone");
        add("Onax");
        add("Ekor");
        add("Afall");
    }};

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
        if (!Load(new TypeReference<>() {})) return;

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
        List<String> names = new ArrayList<>(defaultTerritoryName);
        int halfSize = config.getMapSize() / 2;
        int maxX = halfSize / config.getTerritorySize();
        int maxZ = halfSize / config.getTerritorySize();
        int minX = -maxX;
        int minZ = -maxZ;

        for (int i = minX; i < maxX; ++i) { // -10 inc to 10 exc
            for (int j = minZ; j < maxZ; ++j) { // -10 inc to 10 exc
                int index = Utils.getRandomNumber(0, names.size());
                String name = names.get(index);
                names.remove(index);
                if (CreateTerritory(name, i, j, null, null) == null)
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
                                     Faction faction, IFortification fortification) {
        Territory territory = new Territory(territoryName, posX, posZ, faction, fortification);

        if (!add(territory))
            return null;

        Save();
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

        Save();

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

    public int getDamageFromEnemy(Territory target, Faction enemy) {
        List<Territory> list = getNeighbors(target);
        AtomicInteger count = new AtomicInteger();

        list.stream()
                .filter(territory -> territory.getFaction() != null && territory.getFaction().equals(enemy))
                .forEach(territory -> count.addAndGet(territory.getInfluenceDamage()));

        return count.get();
    }

    public List<Territory> getNeighbors(Territory territory, int range) {
        List<Territory> neighbors = new ArrayList<>();
        int startingX = territory.getPosX() - range;
        int endingX = territory.getPosX() + range;

        for (int x = startingX; x <= endingX; ++x) {
            int diff = range - Math.abs(x);
            int startingZ = territory.getPosZ() - diff;
            int endingZ = territory.getPosZ() + diff;

            for (int z = startingZ; z <= endingZ; ++z) {
                Territory neighbor = get(x, z);

                if (neighbor != null)
                    neighbors.add(get(x, z));
            }
         }

        return neighbors;
    }

    public List<Territory> getNeighbors(Territory territory) {
        List<Territory> neighbors = new ArrayList<>();
        int halfSize = Math.floorDiv(WarOfSquirrels.instance.getConfig().getMapSize(), 2);
        int max = Math.floorDiv(halfSize, WarOfSquirrels.instance.getConfig().getTerritorySize());

        int posX = territory.getPosX();
        int posZ = territory.getPosZ();
        int posXMore = Math.min(posX + 1, max - 1);
        int posXLess = Math.max(posX - 1, -max);
        int posZMore = Math.min(posZ + 1, max - 1);
        int posZLess = Math.max(posZ - 1, -max);

        Territory north = get(posX, posZMore);
        Territory south = get(posX, posZLess);
        Territory east = get(posXMore, posZ);
        Territory west = get(posXLess, posZ);

        if (north != null && !north.equals(territory)) neighbors.add(north);
        if (south != null && !south.equals(territory)) neighbors.add(south);
        if (east != null && !east.equals(territory)) neighbors.add(east);
        if (west != null && !west.equals(territory)) neighbors.add(west);

        return neighbors;
    }

    public Territory get(String name) {
        return dataArray.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst().orElse(null);
    }

    public Territory get(IFortification fortification) {
        return dataArray.stream()
                .filter(t -> t.getFortificationUuid() != null && t.getFortificationUuid().equals(fortification.getUniqueId()))
                .findFirst().orElse(null);
    }

    public Territory get(UUID uuid) {
        return territoryMap.get(uuid);
    }

    public Territory get(Vector2 chunkPos) {
        Pair<Integer, Integer> pos = Utils.ChunkToTerritoryCoordinates((int) chunkPos.x, (int) chunkPos.y);

        return get(pos.getKey(), pos.getValue());
    }

    @CheckForNull
    public Territory get(int posX, int posZ) {
        int halfSize = WarOfSquirrels.instance.getConfig().getMapSize() / 2;
        int size = halfSize / WarOfSquirrels.instance.getConfig().getTerritorySize();
        if (posX < -size || posX >= size || posZ < -size || posZ >= size) return null;

        posX = posX + size;
        posZ = posZ + size;

        return territories[posX][posZ];
    }

    public boolean Claim(int posX, int posZ, Faction faction, IFortification fortification) {
        Territory territory = get(posX, posZ);

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

    public boolean delete(IFortification fortification) {
        get(fortification).reset();

        return true;
    }

    public void update() {
        dataArray.stream().filter(t -> t.getFortification() != null).forEach(Territory::update);

//        for (Territory territory : dataArray) territory.update();
    }

    public void updateDependencies() {
        dataArray.forEach(Territory::updateDependencies);
    }
}
