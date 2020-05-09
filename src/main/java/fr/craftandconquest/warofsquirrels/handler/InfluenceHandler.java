package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InfluenceHandler extends Handler<Influence> {
    private final Map<Faction, Map<Territory, Influence>> influenceMap;
    private final Map<UUID, Influence> influences;

    private static final String DirName = "/WorldData";
    private static final String JsonName = "InfluenceHandler.json";

    public InfluenceHandler(Logger logger) {
        super("[WoS][InfluenceHandler]", logger);
        influenceMap = new HashMap<>();
        influences = new HashMap<>();

        if (!Init()) return;
        if (!Load(new TypeReference<List<Influence>>() {})) return;

        Log();
    }

    @Override
    protected boolean add(Influence value) {
        if (influences.containsKey(value.getUuid()))
            return false;

        influences.put(value.getUuid(), value);

        if (!influenceMap.containsKey(value.getFaction()))
            influenceMap.put(value.getFaction(), new HashMap<>());
        if (influenceMap.get(value.getFaction()).containsKey(value.getTerritory()))
            return false;

        influenceMap.get(value.getFaction()).put(value.getTerritory(), value);
        return true;
    }

    public Influence CreateInfluence(Faction faction, Territory territory) {
        Influence influence = new Influence(faction, territory);

        if (!add(influence))
            return null;

        Save(influences.values());
        LogInfluenceCreation(influence);
        return influence;
    }

    @Override
    public boolean Delete(Influence value) {
        influenceMap.get(value.getFaction()).remove(value.getTerritory());
        influences.remove(value.getUuid());

        Save(influences.values());
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

    public void pushInfluence(Faction faction, Territory territory, int influenceGenerated) {
        Map<Territory, Influence> map = new HashMap<>();
        Influence influence;

        if (influenceMap.containsKey(faction))
            map = influenceMap.get(faction);
        if (map.containsKey(territory))
            influence = map.get(territory);
        else
            influence = CreateInfluence(faction, territory);

        if (influenceGenerated >= 0)
            influence.AddInfluence(influenceGenerated);
        else
            influence.SubInfluence(influenceGenerated);

        Logger.debug(String.format("%s[Influence] Influence généré sur le territoire '%s' : %d (%d)",
                PrefixLogger, territory.getName(), influenceGenerated, influence.getValue()));
    }
}
