package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Diplomacy;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.CustomPermission;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.*;

public class FactionHandler extends UpdatableHandler<Faction> {
    public FactionHandler(Logger logger) {
        super("[WoS][FactionHandler]", logger);
    }

    public Faction CreateFaction(String name, City capital) {
        Faction faction = new Faction(name, capital);

        faction.setCustomPermission(new HashMap<>());
        faction.setDefaultPermission(new HashMap<>(WarOfSquirrels.instance.config.getConfiguration().getPermissionMap()));

        for (Permission permission : faction.getDefaultPermission().values()) {
            permission.setUuid(UUID.randomUUID());
        }

        if (!add(faction)) return null;

        Save();
        LogFactionCreation(faction);

        return faction;
    }

    public Faction get(String name) {
        return dataArray.stream().filter(f -> f.getDisplayName().equals(name)).findFirst().orElse(null);
    }

    @Override
    public boolean Delete(Faction faction) {
        super.Delete(faction);
        WarOfSquirrels.instance.spreadPermissionDelete(faction);

        WarOfSquirrels.instance.getInfluenceHandler().Delete(faction);
        WarOfSquirrels.instance.getTerritoryHandler().Delete(faction);
        for (City city : faction.getCities().values()) {
            city.SetFaction(null);
        }

        Save();
        return true;
    }

    @Override
    public void Log() {
        Logger.info(MessageFormat.format("{0} Factions generated : {1}", PrefixLogger, dataArray.size()));
    }

    @Override
    public void spreadPermissionDelete(IPermission target) {}

    private void LogFactionCreation(Faction faction) {
        Logger.info(PrefixLogger + faction + " created");
    }


    public List<Diplomacy> getDiplomacy(Faction faction, boolean relation) {
        List<Diplomacy> res = new ArrayList<>();
        List<Diplomacy> diplo = WarOfSquirrels.instance.getDiplomacyHandler().get(faction);

        if (diplo != null) {
            res.addAll(diplo.stream().filter(d -> d.isRelation() == relation).toList());
        }
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
            if (d.getTarget().equals(B))
                return true;
        return false;
    }

    public void SetCapital(Faction faction, City city) {
        faction.SetCapital(city);
        MutableComponent message = ChatText.Colored("New capital of faction '" + faction.getDisplayName()
                + "' is now '" + city.getDisplayName() + "'.", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastWorldAnnounce(message);
    }

    public void SetDefaultPermission(PermissionRelation relation, Permission permission, Faction faction) {
        MutableComponent message = new TextComponent("Permission " + relation + " is now set to " + permission);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(faction, null, message, true);
        faction.getDefaultPermission().replace(relation, permission);
        Save();
    }

    public void SetCustomPermission(IPermission target, Permission permission, Faction faction) {
        if (faction.getCustomPermission().containsKey(target)) {
            faction.getCustomPermission().put(target, permission);
            faction.getCustomPermissionList()
                    .stream().filter(e -> e.getTargetUuid().equals(target.getUuid()))
                    .findFirst().get().setPermission(permission);
        } else {
            faction.getCustomPermission().put(target, permission);
            faction.getCustomPermissionList().add(new CustomPermission(
                    target.getUuid(), target.getPermissionTarget(), permission));
        }

    }

    public void updateScore() {
        for (Faction faction: dataArray)
            faction.updateScore();
    }

    @Override
    protected String getDirName() {
        return super.getDirName() + "/Faction";
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
