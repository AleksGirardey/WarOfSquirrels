package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InfluenceHandler extends Handler<Influence> {
    private final Map<City, Map<Territory, Influence>> cityInfluenceMap;
    private final Map<Faction, Map<Territory, Influence>> factionInfluenceMap;
    private final Map<UUID, Influence> influences;

    private static final String DirName = "/WorldData";
    private static final String JsonName = "/InfluenceHandler.json";

    public InfluenceHandler(Logger logger) {
        super("[WoS][InfluenceHandler]", logger);
        cityInfluenceMap = new HashMap<>();
        factionInfluenceMap = new HashMap<>();
        influences = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<>() {
        })) return;

        Log();
    }

    @Override
    protected boolean add(Influence value) {
        if (!dataArray.contains(value)) {
            dataArray.add(value);
        }
        if (!influences.containsKey(value.getUuid()))
            influences.put(value.getUuid(), value);

        if (value.getFaction() != null)
            return AddFactionInfluence(value);
        return AddCityInfluence(value);
    }

    private boolean AddCityInfluence(Influence value) {
        if (!cityInfluenceMap.containsKey(value.getCity()))
            cityInfluenceMap.put(value.getCity(), new HashMap<>());
        if (!cityInfluenceMap.get(value.getCity()).containsKey(value.getTerritory()))
            cityInfluenceMap.get(value.getCity()).put(value.getTerritory(), value);

        return true;
    }

    private boolean AddFactionInfluence(Influence value) {
        if (!factionInfluenceMap.containsKey(value.getFaction()))
            factionInfluenceMap.put(value.getFaction(), new HashMap<>());
        if (!factionInfluenceMap.get(value.getFaction()).containsKey(value.getTerritory()))
            factionInfluenceMap.get(value.getFaction()).put(value.getTerritory(), value);
        return true;
    }

    public Influence CreateInfluence(Faction faction, Territory territory) {
        Influence influence = new Influence(faction, territory);

        if (!add(influence))
            return null;

        Save();
        LogInfluenceCreation(influence);
        return influence;
    }

    public Influence CreateInfluence(City city, Territory territory) {
        Influence influence = new Influence(city, territory);

        if (!add(influence)) return null;

        Save();
        LogInfluenceCreation(influence);
        return influence;
    }

    @Override
    public boolean Delete(Influence value) {
        factionInfluenceMap.get(value.getFaction()).keySet().removeIf(t -> t.equals(value.getTerritory()));
        influences.remove(value.getUuid());

        Save();
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Influences generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    public void LogInfluenceCreation(Influence influence) {
        Logger.info(String.format("%s %s created !", PrefixLogger, influence));
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

    public Influence get(UUID uuid) {
        return influences.get(uuid);
    }

    public Influence get(Faction faction, Territory territory) {
        return factionInfluenceMap.get(faction).get(territory);
    }

    public Influence get(City city, Territory territory) {
        return cityInfluenceMap.get(city).get(territory);
    }

    public void ResetOthersInfluence(Territory territory) {
        cityInfluenceMap.forEach((k, v) -> v.keySet().removeIf(t -> t.equals(territory)));
        factionInfluenceMap.forEach((k, v) -> {
            if (k != territory.getFaction()) {
                if (v.containsKey(territory))
                    v.get(territory).SubInfluence(v.get(territory).getValue());
            }
        });
    }

    public void pushInfluence(IFortification fortification, Territory territory, int influenceGenerated) {
        Map<Territory, Influence> map = new HashMap<>();
        Influence influence;

        boolean hasFaction = fortification.getFaction() != null;

        if (hasFaction) {
            Faction faction = fortification.getFaction();

            if (factionInfluenceMap.containsKey(faction))
                map = factionInfluenceMap.get(faction);
            if (map.containsKey(territory))
                influence = map.get(territory);
            else
                influence = CreateInfluence(faction, territory);
        } else {
            City city = fortification.getRelatedCity();

            if (cityInfluenceMap.containsKey(city)) {
                map = cityInfluenceMap.get(city);
            }
            if (map.containsKey(territory)) {
                influence = map.get(territory);
            } else {
                influence = CreateInfluence(city, territory);
            }
        }
        pushInfluence(influence, influenceGenerated);
    }

    public void pushInfluence(Influence influence, int influenceGenerated) {
        if (influenceGenerated >= 0) {
            influence.AddInfluence(influenceGenerated);
        }
        else {
            influence.SubInfluence(influenceGenerated);
        }

        Logger.debug(String.format("%s[Influence] Influence généré sur le territoire '%s' : %d (%d)",
                PrefixLogger, influence.getTerritory().getName(), influenceGenerated, influence.getValue()));
    }

    public void Delete(Faction faction) {
        Map<Territory, Influence> factionInfluence = factionInfluenceMap.get(faction);

        for (Influence influence : factionInfluence.values())
            dataArray.remove(influence);

        factionInfluenceMap.keySet().removeIf(f -> f.equals(faction));
        Save();
    }

    public List<Influence> getAll(Territory territory) {
        return dataArray.stream().filter(influence -> influence.getTerritory().equals(territory)).toList();
    }

    public void updateDependencies() {
        dataArray.forEach(i-> {
            i.updateDependencies();
            add(i);
        });
    }
}















