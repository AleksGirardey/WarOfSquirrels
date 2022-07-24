package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.config.ConfigData;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import org.apache.logging.log4j.Logger;

import javax.annotation.CheckForNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TerritoryHandler extends UpdatableHandler<Territory> {
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

        // 40
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

        // 50
        add("Ifkar");
        add("Aderma");
        add("Iomedae");
        add("Helharian");
        add("Thorstein");
        add("Ultan");
        add("Avendrah");
        add("Deles");
        add("Anthil");
        add("Marmousset");

        // 60
        add("Esand");
        add("Kayoa");
        add("Opes");
        add("Ceruth");
        add("Vaegir");
        add("Shomos");
        add("Lorvald");
        add("Ezora");
        add("Itios");
        add("Toceron");

        // 70
        add("Ikora");
        add("Ocone");
        add("Anoris");
        add("Iadresh");
        add("Adajin");
        add("Anskylvia");
        add("Ichiver");
        add("Yavealan");
        add("Eoyarim");
        add("Citemare");

        // 80
        add("Capalis");
        add("Lufiros");
        add("Everglade");
        add("Haven");
        add("Ataes");
        add("Esis");
        add("Vrafor");
        add("Slecox");
        add("Plizin");
        add("Ohux");

        // 90
        add("Minn");
        add("Zun");
        add("Heiz");
        add("Sun");
        add("Thalon");
        add("Lavua");
        add("Ivius");
        add("Acalen");
        add("Melovan");
        add("Calade");

        // 100
        add("Dionandra");
        add("Gala");
        add("Acanthy");
        add("Acaly");
        add("Ilee");
        add("Astien");
        add("Lotia");
        add("Angiss");
        add("Ivin");
        add("Malor");
    }};

    private Map<Faction, List<Territory>> territoriesByFaction;
    private Territory[][] territories;

    public TerritoryHandler(Logger logger) {
        super("[WoS][TerritoryHandler]", logger);

        //WarOfSquirrels.instance.debugLog("Territories : " + (territories != null ? "NOT NULL" : "NULL"));
    }

    @Override
    protected void InitVariables() {
        int sizeMap = WarOfSquirrels.instance.config.getConfiguration().getMapSize() /
                WarOfSquirrels.instance.config.getConfiguration().getTerritorySize();
        territories = new Territory[sizeMap][sizeMap];
        territoriesByFaction = new HashMap<>();
    }

    @Override
    protected boolean Load() {
        String errorMessage = String.format("%s Couldn't load Json data.", PrefixLogger);

        try {
            if (new BufferedReader(new FileReader(getConfigPath())).readLine() != null) {
                dataArray = jsonArrayToList(configFile, Territory.class);
                if (!Populate()) {
                    Logger.error(errorMessage + " (Populate)");
                    return false;
                }
            } else {
                Generate();
            }
        } catch (IOException e) {
            Logger.error(errorMessage + "'" + getConfigPath() + "' (File creation) - " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    protected void CustomLoad(File configFile) throws IOException {
        dataArray = jsonArrayToList(configFile, Territory.class);
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
        Save();
    }


    public boolean addPopulated(Territory territory) {
        if (!dataArray.contains(territory)) {
            if (dataArray.size() == 0)
                dataArray = new ArrayList<>();
            dataArray.add(territory);
        }

        int offset = (WarOfSquirrels.instance.getConfig().getMapSize() / 2) / WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posX = territory.getPosX() + offset;
        int posZ = territory.getPosZ() + offset;

        territories[posX][posZ] = territory;

        if (territory.getFaction() == null) return true;

        if (territoriesByFaction == null) territoriesByFaction = new HashMap<>();

        if (!territoriesByFaction.containsKey(territory.getFaction()))
            territoriesByFaction.put(territory.getFaction(), new ArrayList<>());
        if (!territoriesByFaction.get(territory.getFaction()).contains(territory))
            territoriesByFaction.get(territory.getFaction()).add(territory);

        return true;
    }

    public Territory CreateTerritory(String territoryName, int posX, int posZ,
                                     Faction faction, IFortification fortification) {
        Territory territory = new Territory(territoryName, posX, posZ, faction, fortification);

        if (!add(territory))
            return null;
        LogTerritoryCreation(territory);

        return territory;
    }

    @Override
    public boolean Delete(Territory territory) {
        super.Delete(territory);
        int offset = (WarOfSquirrels.instance.getConfig().getMapSize() / 2) / WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posX = territory.getPosX() + offset;
        int posZ = territory.getPosZ() + offset;

        territories[posX][posZ] = null;

        Save();

        return true;
    }

    public void LogTerritoryCreation(Territory territory) {
        Logger.info(PrefixLogger + " " + territory.getDisplayName() + " created" + (territory.getBiome().isHasRiver() ? " with river." : ""));
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Territories generated : {1}",
                PrefixLogger, dataArray.size()));
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
        if (range == 0) return Collections.emptyList();
        if (range == 1) return getNeighbors(territory);

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
                .filter(t -> t.getDisplayName().equals(name))
                .findFirst().orElse(null);
    }

    public Territory get(IFortification fortification) {
        Vector2 territoryPos = fortification.getTerritoryPosition();

        return dataArray.stream()
                .filter(t ->
                        (t.getFortificationUuid() != null && t.getFortificationUuid().equals(fortification.getUuid())) ||
                        (t.getPosX() == territoryPos.x && t.getPosZ() == territoryPos.y))
                .findFirst()
                .orElse(null);
    }

    public Territory getFromTerritoryPos(Vector2 territory) {
        return get((int) territory.x, (int) territory.y);
    }

    public Territory getFromChunkPos(Vector2 chunkPos) {
        Vector2 pos = Utils.FromChunkToTerritory((int) chunkPos.x, (int) chunkPos.y);

        return getFromTerritoryPos(pos);
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
        if (territory.getFortification() != null && territory.getFortification() != fortification) return false;

        territory.SetFaction(faction);
        territory.SetFortification(fortification);

        if (!territoriesByFaction.containsKey(faction))
            territoriesByFaction.put(faction, new ArrayList<>());
        territoriesByFaction.get(faction).add(territory);

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

    @Override
    public void update() {
        dataArray.stream().filter(t -> t.getFortification() != null).forEach(Territory::update);
    }

    @Override
    public void updateDependencies() {
        dataArray.forEach(Territory::updateDependencies);
        dataArray.forEach(this::addPopulated);
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/World";
    }
}
