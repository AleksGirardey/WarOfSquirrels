package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Diplomacy;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FactionHandler extends Handler<Faction> {
    private final Map<UUID, Faction> factionMap;

    protected static String DirName = "/WorldData";
    protected static String JsonName = "/FactionHandler.json";

    public FactionHandler(Logger logger) {
        super("[WoS][FactionHandler]", logger);
        factionMap = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Faction>>() {
        })) return;

        Log();
    }

    public void updateDependencies() {
        factionMap.values().forEach(Faction::updateDependencies);
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    public boolean add(Faction faction) {
        if (factionMap.containsKey(faction.getFactionUuid())) return false;

        if (!dataArray.contains(faction)) {
            if (dataArray.size() == 0) dataArray = new ArrayList<Faction>();

            dataArray.add(faction);
        }

        factionMap.put(faction.getFactionUuid(), faction);

        return true;
    }

    public Faction CreateFaction(String name, City capital) {
        Faction faction = new Faction(name, capital);

        if (!add(faction)) return null;

        Save();
        LogFactionCreation(faction);

        return faction;
    }

    public Faction get(UUID uuid) {
        return factionMap.get(uuid);
    }

    public Faction get(String name) {
        for (Faction faction : factionMap.values()) {
            if (faction.getDisplayName().equals(name))
                return faction;
        }

        return null;
    }

    public List<Faction> getAll() {
        return dataArray;
    }

    @Override
    public boolean Delete(Faction faction) {
        WarOfSquirrels.instance.spreadPermissionDelete(faction);

        WarOfSquirrels.instance.getInfluenceHandler().Delete(faction);
        WarOfSquirrels.instance.getTerritoryHandler().Delete(faction);
        for (City city : faction.getCities().values())
            city.SetFaction(null);

        factionMap.remove(faction.getFactionUuid());

        Save();
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

    private void LogFactionCreation(Faction faction) {
        Logger.info(PrefixLogger + faction + " created");
    }


    public List<Diplomacy> getDiplomacy(Faction faction, boolean relation) {
        List<Diplomacy> res = new ArrayList<>(),
                diplo = WarOfSquirrels.instance.getDiplomacyHandler().get(faction);

        if (diplo != null)
            res.addAll(diplo.stream().filter(d -> d.isRelation() == relation).collect(Collectors.toList()));
        return res;
    }

    public boolean areAllies(Faction A, Faction B) {
        List<Diplomacy> diploA = getDiplomacy(A, true);

        for (Diplomacy d : diploA)
            if (d.getTarget() == B)
                return true;
        return false;
    }

    public boolean areEnemies(Faction A, Faction B) {
        List<Diplomacy> diploA = getDiplomacy(A, false);

        for (Diplomacy d : diploA)
            if (d.getTarget() == B)
                return true;
        return false;
    }

    public void SetCapital(Faction faction, City city) {
        faction.SetCapital(city);
        MutableComponent message = ChatText.Colored("La nouvelle capitale de '" + faction.getDisplayName()
                + "' est désormais la ville de '" + city.getDisplayName() + "'.", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
    }
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