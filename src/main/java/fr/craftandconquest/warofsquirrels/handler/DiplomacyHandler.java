package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.faction.Diplomacy;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class DiplomacyHandler extends Handler<Diplomacy> {
    private final Map<Faction, List<Diplomacy>> diplomacyMap = new HashMap<>();

    public DiplomacyHandler(Logger logger) {
        super("[WoS][DiplomacyHandler]", logger);
    }

    @Override
    protected boolean add(Diplomacy value) {
        super.add(value);

        if (!diplomacyMap.containsKey(value.getFaction()))
            diplomacyMap.put(value.getFaction(), new ArrayList<>());
        if (!diplomacyMap.get(value.getFaction()).contains(value))
            diplomacyMap.get(value.getFaction()).add(value);

        return true;
    }

    @Override
    public boolean Delete(Diplomacy value) {
        super.Delete(value);

        diplomacyMap.get(value.getFaction()).remove(value);

        return true;
    }

    public boolean Delete(Faction faction) {
        if (!diplomacyMap.containsKey(faction)) return false;

        List<Diplomacy> toBeRemoved = diplomacyMap.get(faction);

        for (Diplomacy diplomacy : toBeRemoved)
            Delete(diplomacy);

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

        diplomacy.setUuid(UUID.randomUUID());
        diplomacy.SetFaction(main);
        diplomacy.SetTarget(target);
        diplomacy.setRelation(relation);
        diplomacy.SetPermission(permission);

        add(diplomacy);
        Save();
    }

    public List<Diplomacy> get(Faction faction) {
        return diplomacyMap.get(faction);
    }


    @Override
    public void updateDependencies() {
        dataArray.forEach(Diplomacy::updateDependencies);
        Populate();
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Diplomacy generated : {1}",
                PrefixLogger, dataArray.size()));
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction";
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {}
}
