package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class DiplomacyHandler {
    private Logger logger;

    private Map<Integer, Diplomacy>                 diplomacies = new HashMap<>();
    private Map<Faction, List<Diplomacy>>           diplomacyMap = new HashMap<>();

    @Inject
    public DiplomacyHandler(Logger logger) {
        this.logger = logger;
    }

    public void        populate() {
        String          sql = "SELECT * FROM `" + GlobalDiplomacy.tableName + "`;";
        Diplomacy       diplomacy;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                diplomacy = new Diplomacy(statement.getResult());
                this.diplomacies.put(diplomacy.getId(), diplomacy);
                if (!diplomacyMap.containsKey(diplomacy.getFaction()))
                    this.diplomacyMap.put(diplomacy.getFaction(), new ArrayList<>());
                this.diplomacyMap.get(diplomacy.getFaction()).add(diplomacy);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void                     add(Faction main, Faction sub, boolean relation, Permission perm) {
        Diplomacy                   d = new Diplomacy(main, sub, relation, perm);

        diplomacies.put(d.getId(), d);
        if (!diplomacyMap.containsKey(main))
            diplomacyMap.put(main, new ArrayList<>());
        diplomacyMap.get(main).add(d);
    }

    public Diplomacy                get(int id) { return diplomacies.get(id); }

    public List<Diplomacy>          get(Faction faction) {
        return diplomacyMap.get(faction);
    }

    public void                     delete(Diplomacy diplomacy) {
        Diplomacy                   target;

        diplomacies.remove(diplomacy.getId());
        diplomacyMap.get(diplomacy.getFaction()).remove(diplomacy);

        if (diplomacy.getRelation()) {
            List<Diplomacy>     list = diplomacyMap.get(diplomacy.getTarget());
            Diplomacy           diplo = list.stream()
                    .filter(d -> d.getTarget().equals(diplomacy.getFaction())).findFirst().orElse(null);

            if (diplo != null) {
                diplomacies.remove(diplo.getId());
                diplomacyMap.get(diplo.getFaction());
                diplo.delete();
            }
        }
        diplomacy.delete();
    }

    public void                     delete(Faction faction) {
        if (!diplomacyMap.containsKey(faction))
            return;

        List<Diplomacy>     list = diplomacyMap.get(faction);

        for (Diplomacy d : list)
            delete(d);
        diplomacyMap.remove(faction);
    }

    public List<Faction>               getEnemies(Faction faction) {
        if (diplomacyMap.containsKey(faction))
            return diplomacyMap.get(faction).stream().filter(d -> !d.getRelation()).map(Diplomacy::getTarget).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public List<Faction>               getAllies(Faction faction) {
        if (diplomacyMap.containsKey(faction))
            return diplomacyMap.get(faction).stream().filter(Diplomacy::getRelation).map(Diplomacy::getTarget).collect(Collectors.toList());
        return Collections.emptyList();
    }

    public String       getAlliesAsString(Faction faction) {
        String          message = "";
        List<Faction>      list = getAllies(faction);
        int             i = 0, max = list.size();

        for (Faction f : list) {
            message += f.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }

    public String       getEnemiesAsString(Faction faction) {
        String          message = "";
        List<Faction>   list = getEnemies(faction);
        int             i = 0, max = list.size();

        for (Faction f : list) {
            message += f.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }
}