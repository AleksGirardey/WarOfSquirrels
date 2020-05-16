package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Diplomacy;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DiplomacyHandler extends Handler<Diplomacy> {
    private Map<UUID, Diplomacy> diplomacyByUuid;
    private Map<Faction, List<Diplomacy>> diplomacyMap;

    private static final String DirName = "/WorldData";
    private static final String JsonName = "/DiplomacyHandler.json"

    public DiplomacyHandler(Logger logger) {
        super("[WoS][DiplomacyHandler]", logger);
    }

    @Override
    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    @Override
    protected boolean add(Diplomacy value) {
        return false;
    }

    @Override
    public boolean Delete(Diplomacy value) {
        return false;
    }

    public Diplomacy CreateDiplomacy(Faction main, Faction target, boolean relation, Permission permission) {
        Diplomacy diplomacy = new Diplomacy();

        diplomacy.SetFaction(main);
        diplomacy.SetTarget(target);
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
