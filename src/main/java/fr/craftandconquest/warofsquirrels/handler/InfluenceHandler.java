package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfluenceHandler extends Handler<Influence> {
    private final Map<City, Map<Territory, Influence>> cityInfluenceMap = new HashMap<>();
    private final Map<Faction, Map<Territory, Influence>> factionInfluenceMap = new HashMap<>();

    public InfluenceHandler(Logger logger) {
        super("[WoS][InfluenceHandler]", logger);
    }

    @Override
    protected boolean add(Influence value) {
        super.add(value);

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
        super.Delete(value);

        if (value.getFaction() != null) {
            factionInfluenceMap.get(value.getFaction()).keySet().removeIf(t -> t.equals(value.getTerritory()));
        }
        else if (cityInfluenceMap.containsKey(value.getCity())) {
            cityInfluenceMap.get(value.getCity()).keySet().removeIf(t -> t.equals(value.getTerritory()));
        }

        Save();
        return true;
    }

    public boolean delete(City city) {
        List<Influence> toBeDeleted = new ArrayList<>();
        Faction faction = city.getFaction();

        if (faction != null) {
            Delete(faction);
        } else {
            if (cityInfluenceMap.containsKey(city)) {
                toBeDeleted.addAll(cityInfluenceMap.get(city).values());
                cityInfluenceMap.remove(city);
            }
        }

        toBeDeleted.forEach(this::Delete);
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
    public void spreadPermissionDelete(IPermission target) {}

    public Influence get(Faction faction, Territory territory) {
        if (factionInfluenceMap.get(faction) != null)
            return factionInfluenceMap.get(faction).get(territory);
        return null;
    }

    public Influence get(City city, Territory territory) {
        if (cityInfluenceMap.get(city) != null)
            return cityInfluenceMap.get(city).get(territory);
        return null;
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
                PrefixLogger, influence.getTerritory().getDisplayName(), influenceGenerated, influence.getValue()));
    }

    public void SwitchInfluence(City city, Faction faction, Territory territory) {
        Influence old = get(city, territory);
        Influence influence = CreateInfluence(faction, territory);

        if (old == null) return;

        influence.setValue(old.getValue());

        Delete(old);
    }

    public void Delete(Faction faction) {
        Map<Territory, Influence> factionInfluence = factionInfluenceMap.get(faction);

        if (factionInfluence == null) return;

        for (Influence influence : factionInfluence.values())
            dataArray.remove(influence);

        factionInfluenceMap.keySet().removeIf(f -> f.equals(faction));
        Save();
    }

    public List<Influence> getAll(Territory territory) {
        return dataArray.stream().filter(influence -> influence.getTerritory().equals(territory)).toList();
    }

    @Override
    public void updateDependencies() {
        dataArray.forEach(i-> {
            i.updateDependencies();
            add(i);
        });
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction";
    }
}















