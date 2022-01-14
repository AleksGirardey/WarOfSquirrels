package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Diplomacy;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DiplomacyHandler extends Handler<Diplomacy> {
    private Map<UUID, Diplomacy> diplomacyByUuid = new HashMap<>();
    private Map<Faction, List<Diplomacy>> diplomacyMap = new HashMap<>();

    private static final String DirName = "/WorldData";
    private static final String JsonName = "/DiplomacyHandler.json";

    public DiplomacyHandler(Logger logger) {
        super("[WoS][DiplomacyHandler]", logger);

        if (!Init()) return;
        if (!Load(new TypeReference<List<Diplomacy>>() {
        })) return;

        Log();
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    @Override
    protected boolean add(Diplomacy value) {
        if (dataArray.contains(value)) return false;

        dataArray.add(value);
        diplomacyByUuid.put(value.getUuid(), value);
        if (!diplomacyMap.containsKey(value.getFaction()))
            diplomacyMap.put(value.getFaction(), new ArrayList<>());
        diplomacyMap.get(value.getFaction()).add(value);

        return true;
    }

    @Override
    public boolean Delete(Diplomacy value) {
        return Delete(value, true);
    }

    public boolean Delete(Diplomacy value, boolean save) {
        if (!dataArray.contains(value)) return false;

        dataArray.remove(value);
        diplomacyByUuid.remove(value.getUuid());
        diplomacyMap.get(value.getFaction()).remove(value);

        if (save) Save();

        return true;
    }

    public boolean Delete(Faction faction) {
        if (!diplomacyMap.containsKey(faction)) return false;

        List<Diplomacy> toBeRemoved = diplomacyMap.get(faction);

        for (Diplomacy diplomacy : toBeRemoved)
            Delete(diplomacy, false);

        diplomacyMap.keySet().removeIf(f -> f.equals(faction));
        Save();

        return true;
    }

    public List<Faction> getEnemies(Faction faction) {
        return getFromRelation(faction, false);
    }

    public List<Faction> getAllies(Faction faction) {
        return getFromRelation(faction, true);
    }

    public List<Faction> getFromRelation(Faction faction, boolean relation) {
        if (diplomacyMap.containsKey(faction))
            return diplomacyMap.get(faction).stream()
                    .filter(d -> d.isRelation() == relation)
                    .map(Diplomacy::getTarget)
                    .collect(Collectors.toList());
        return Collections.emptyList();
    }

    public String getRelationAsString(Faction faction, boolean relation) {
        StringBuilder message = new StringBuilder();
        List<Faction> toBeDisplayed = relation ? getAllies(faction) : getEnemies(faction);

        int i = 0, max = toBeDisplayed.size();

        for (Faction f : toBeDisplayed) {
            message.append(f.getDisplayName());
            if (i != max - 1)
                message.append(", ");
        }
        return message.toString();
    }

    public void SetNeutral(Faction main, Faction target) {
        diplomacyMap.get(main).removeIf(diplomacy -> diplomacy.getTarget() == target);
        diplomacyMap.get(target).removeIf(diplomacy -> diplomacy.getTarget() == main && diplomacy.isRelation());
    }

    public void CreateDiplomacy(Faction main, Faction target, boolean relation, Permission permission) {
        Diplomacy diplomacy = new Diplomacy();

        diplomacy.SetFaction(main);
        diplomacy.SetTarget(target);
        diplomacy.setRelation(relation);
        diplomacy.SetPermission(permission);

        add(diplomacy);
        Save();
    }

    public Diplomacy get(UUID uuid) {
        return diplomacyByUuid.get(uuid);
    }

    public List<Diplomacy> get(Faction faction) {
        return diplomacyMap.get(faction);
    }

    public void updateDependencies() {
        dataArray.forEach(Diplomacy::updateDependencies);
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Diplomacy generated : {1}",
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
        // Nothing to do
    }
}
