package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiplomacyHandler {
    private Logger logger;

    private Map<Integer, Diplomacy>             diplomacies = new HashMap<>();
    private Map<City, List<Diplomacy>>          diplomacyMap = new HashMap<>();

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
                if (!diplomacyMap.containsKey(diplomacy.getMain()))
                    this.diplomacyMap.put(diplomacy.getMain(), new ArrayList<>());
                this.diplomacyMap.get(diplomacy.getMain()).add(diplomacy);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void                     add(City main, City sub, boolean relation, Permission perm) {
        Diplomacy                   d = new Diplomacy(main, sub, relation, perm);

        diplomacies.put(d.getId(), d);
        if (!diplomacyMap.containsKey(main))
            diplomacyMap.put(main, new ArrayList<>());
        diplomacyMap.get(main).add(d);
    }

    public Diplomacy                get(int id) { return diplomacies.get(id); }

    public List<Diplomacy>          get(City city) {
        List<Diplomacy>             list = new ArrayList<>(diplomacyMap.get(city));

        diplomacies.values().stream().filter(d -> !list.contains(d) && (d.getSub() == city)).forEach(list::add);

        return list;
    }

    public void                     delete(int id) {
        diplomacies.remove(id);
        diplomacyMap.values().stream().filter(d -> d.contains(get(id))).forEach(d -> d.remove(get(id)));
    }

    public void                     delete(City city) {
        if (!diplomacyMap.containsKey(city))
            return;
        for (Diplomacy d : diplomacyMap.get(city)) {
            diplomacies.remove(d.getId());
            d.delete();
        }
        diplomacyMap.remove(city);
    }

    public List<City>               getEnemies(City city) {
        List<City>                  list = new ArrayList<>();

        diplomacies.values().stream().filter(d -> !d.getRelation()).forEach(d -> {
            if (d.getMain() == city)
                list.add(d.getSub());
            else if (d.getSub() == city)
                list.add(d.getMain());
        });
        return list;
    }

    public List<City>               getAllies(City city) {
        List<City>                  list = new ArrayList<>();

        diplomacies.values().stream().filter(Diplomacy::getRelation).forEach(d -> {
            if (d.getMain() == city)
                list.add(d.getSub());
            else if (d.getSub() == city)
                list.add(d.getMain());
        });
        return list;
    }

    public String       getAlliesAsString(City city) {
        String          message = "";
        List<City>      list = getAllies(city);
        int             i = 0, max = list.size();

        for (City c : list) {
            message += c.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }

    public String       getEnemiesAsString(City city) {
        String          message = "";
        List<City>      list = getEnemies(city);
        int             i = 0, max = list.size();

        for (City c : list) {
            message += c.getDisplayName();
            if (i != max - 1)
                message += ", ";
        }
        return message;
    }
}