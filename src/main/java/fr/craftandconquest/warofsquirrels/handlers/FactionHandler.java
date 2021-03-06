package fr.craftandconquest.warofsquirrels.handlers;

import com.google.inject.Inject;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalFaction;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import fr.craftandconquest.warofsquirrels.objects.faction.InfoFaction;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class                        FactionHandler {

    private Logger                  logger;
    private Map<Integer, Faction>   factions = new HashMap<>();

    @Inject
    public FactionHandler(Logger logger) { this.logger = logger; }

    public void         populate() {
        String          sql = "SELECT * FROM `" + GlobalFaction.tableName + "`";
        Faction         faction;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                faction = new Faction(statement.getResult());
                this.factions.put(faction.getId(), faction);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void         updateDependencies() {
        factions.values().forEach(Faction::updateDependencies);
    }

    public Faction      get(int id) { return factions.get(id); }

    public Faction      get(String name) { return factions.values().stream().filter(f -> f.getDisplayName().equals(name)).findFirst().orElse(null); }

    public Faction      add(City city, String displayName) {
        Faction         faction = new Faction(displayName, city);

        factions.put(faction.getId(), faction);
        return faction;
    }

    public boolean             delete(Faction faction) {
        Collection<City>    cities = faction.getCities().values();
        logger.info("[faction][Delete] Found " + cities.size() + " cities related to faction '" + faction.getDisplayName() + "'");
        factions.remove(faction.getId());
        faction.setCapital(null);
        cities.forEach(c -> Core.getCityHandler().delete(c));
        Core.getDiplomacyHandler().delete(faction);
        faction.delete();
        return true;
    }

    public List<Diplomacy>      getDiplomacy(Faction faction, boolean relation) {
        List<Diplomacy>         res = new ArrayList<>(),
                diplo = Core.getDiplomacyHandler().get(faction);

        if (diplo != null) res.addAll(diplo.stream().filter(d -> d.getRelation() == relation).collect(Collectors.toList()));
        return res;
    }

    public boolean          areAllies(Faction A, Faction B) {
        List<Diplomacy>     diploA = getDiplomacy(A, true);

        for (Diplomacy d : diploA)
            if (d.getTarget() == B)
                return true;
        return false;
    }

    public boolean          areEnemies(Faction A, Faction B) {
        List<Diplomacy>     diploA = getDiplomacy(A, false);

        for (Diplomacy d : diploA)
            if (d.getTarget() == B)
                return true;
        return false;
    }

    public void             setNeutral(Faction A, Faction B) {
        List<Diplomacy>     list = new ArrayList<>();

        list.addAll(getDiplomacy(A, true));
        list.addAll(getDiplomacy(A, false));

        for (Diplomacy d : list) {
            if (d.getTarget() == B)
                Core.getDiplomacyHandler().delete(d);
        }
    }

    public List<String>     getEnemiesName(Faction faction) {
        List<String>        list = new ArrayList<>();
        List<Faction>       factions = Core.getDiplomacyHandler().getEnemies(faction);

        list.addAll(factions.stream().map(Faction::getDisplayName).collect(Collectors.toList()));
        return list;
    }

    public List<String>     getAlliesName(Faction faction) {
        List<String>        list = new ArrayList<>();
        List<Faction>       factions = Core.getDiplomacyHandler().getAllies(faction);

        list.addAll(factions.stream().map(Faction::getDisplayName).collect(Collectors.toList()));
        return list;
    }

    public List<String> getFactionNameList() { return factions.values().stream().map(Faction::getDisplayName).collect(Collectors.toList()); }

    public Map<Faction,InfoFaction> getFactionMap() {
        Map<Faction, InfoFaction> map;

        map = factions.values().stream().collect(Collectors.toMap(f -> f, InfoFaction::new));
        factions.values().forEach(f -> logger.info("[InfoFaction] new faction info created for `" + f.getDisplayName() + "`."));

        return map;
    }

    public Map<String,Attackable> getAttackables(Faction faction) {
        Map<String, Attackable>     attackables = new HashMap<>();

        attackables.putAll(Core.getCityHandler().getAttackables(faction));

        return attackables;
    }
}

