package fr.craftandconquest.warofsquirrels.handlers;

import fr.craftandconquest.warofsquirrels.objects.database.GlobalInfluence;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Influence;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Territory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class InfluenceHandler {

    private Map<Faction, Map<Territory, Influence>>     influenceMap = new HashMap<>();
    private Map<Integer, Influence>                     influences = new HashMap<>();

    public void         populate() {
        String sql = "SELECT * FROM `" + GlobalInfluence.TABLENAME + "`";
        Influence influence;

        try {
            Statement statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                influence = new Influence(statement.getResult());
                influences.put(influence.getId(), influence);
                if (!influenceMap.containsKey(influence.getFaction()))
                    influenceMap.put(influence.getFaction(), new HashMap<>());
                influenceMap.get(influence.getFaction()).put(influence.getTerritory(), influence);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Influence        get(int id) { return influences.get(id); }

    public Influence        get(Faction faction, Territory territory) {
        return influenceMap.get(faction).get(territory);
    }

    public void pushInfluence(Territory territory, int influenceGenerated) {
        Map<Territory, Influence> map = new HashMap<>();
        Influence   influence;

        if (influenceMap.containsKey(territory.getFaction()))
            map = influenceMap.get(territory.getFaction());
        if (map.containsKey(territory)) {
            influence = map.get(territory);
        } else
            influence = new Influence(territory.getFaction(), territory);

        if (influenceGenerated >= 0)
            influence.addInfluence(influenceGenerated);
        else influence.subInfluence(influenceGenerated);

        map.putIfAbsent(territory, influence);
        influenceMap.putIfAbsent(territory.getFaction(), map);
    }
}