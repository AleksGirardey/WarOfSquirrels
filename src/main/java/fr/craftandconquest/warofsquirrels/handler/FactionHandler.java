package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class FactionHandler extends Handler<Faction> {
    private Map<UUID, Faction> factionMap;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/FactionHandler.json";

    protected FactionHandler(Logger logger) {
        super("[WoS][PlayerHandler]", logger);
        factionMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Faction>>() {})) return;

        Log();
    }

    public void updateDependencies() {
        factionMap.values().forEach(Faction::updateDependencies);
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return false;
    }

    public boolean add(Faction faction) {
        if (factionMap.containsKey(faction.getFactionUuid())) return false;

        if (!dataArray.contains(faction)) {
            if (dataArray.size() == 0) dataArray = new ArrayList<Faction>();

            dataArray.add(faction);
        }

        factionMap.put(faction.getFactionUuid(), faction);
        Save(factionMap.values());
        LogFactionCreation(faction);
        return true;
    }

    public Faction CreateFaction(String name, City capital) {
        Faction faction = new Faction(name, capital);

        if (!add(faction)) return null;

        return faction;
    }

    public Faction get(UUID uuid) {
        return factionMap.get(uuid);
    }

    public Faction get(String name) {
        for(Faction faction : factionMap.values()) {
            if (faction.getDisplayName().equals(name))
                return faction;
        }

        return null;
    }

    @Override
    public boolean Delete(Faction faction) {
        WarOfSquirrels.instance.spreadPermissionDelete(faction);

        for (City city : faction.getCities().values())
            WarOfSquirrels.instance.getCityHandler().Delete(city);

        factionMap.remove(faction.getFactionUuid());

        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Factions generated : {1}",
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

    private void LogFactionCreation(Faction faction) { Logger.info(PrefixLogger + faction + " created"); }


//    public List<Diplomacy>      getDiplomacy(Faction faction, boolean relation) {
//        List<Diplomacy>         res = new ArrayList<>(),
//                diplo = Core.getDiplomacyHandler().get(faction);
//
//        if (diplo != null) res.addAll(diplo.stream().filter(d -> d.getRelation() == relation).collect(Collectors.toList()));
//        return res;
//    }
//
//    public boolean          areAllies(Faction A, Faction B) {
//        List<Diplomacy>     diploA = getDiplomacy(A, true);
//
//        for (Diplomacy d : diploA)
//            if (d.getTarget() == B)
//                return true;
//        return false;
//    }
//
//    public boolean          areEnemies(Faction A, Faction B) {
//        List<Diplomacy>     diploA = getDiplomacy(A, false);
//
//        for (Diplomacy d : diploA)
//            if (d.getTarget() == B)
//                return true;
//        return false;
//    }
//
//    public void             setNeutral(Faction A, Faction B) {
//        List<Diplomacy>     list = new ArrayList<>();
//
//        list.addAll(getDiplomacy(A, true));
//        list.addAll(getDiplomacy(A, false));
//
//        for (Diplomacy d : list) {
//            if (d.getTarget() == B)
//                Core.getDiplomacyHandler().delete(d);
//        }
//    }
//
//    public List<String>     getEnemiesName(Faction faction) {
//        List<String>        list = new ArrayList<>();
//        List<Faction>       factions = Core.getDiplomacyHandler().getEnemies(faction);
//
//        list.addAll(factions.stream().map(Faction::getDisplayName).collect(Collectors.toList()));
//        return list;
//    }
//
//    public List<String>     getAlliesName(Faction faction) {
//        List<String>        list = new ArrayList<>();
//        List<Faction>       factions = Core.getDiplomacyHandler().getAllies(faction);
//
//        list.addAll(factions.stream().map(Faction::getDisplayName).collect(Collectors.toList()));
//        return list;
//    }
//
//    public List<String> getFactionNameList() { return factions.values().stream().map(Faction::getDisplayName).collect(Collectors.toList()); }
//
//    public Map<Faction,InfoFaction> getFactionMap() {
//        Map<Faction, InfoFaction> map;
//
//        map = factions.values().stream().collect(Collectors.toMap(f -> f, InfoFaction::new));
//        factions.values().forEach(f -> logger.info("[InfoFaction] new faction info created for `" + f.getDisplayName() + "`."));
//
//        return map;
//    }
//
//    public Map<String,Attackable> getAttackables(Faction faction) {
//        Map<String, Attackable>     attackables = new HashMap<>();
//
//        attackables.putAll(Core.getCityHandler().getAttackables(faction));
//
//        return attackables;
//    }
}
