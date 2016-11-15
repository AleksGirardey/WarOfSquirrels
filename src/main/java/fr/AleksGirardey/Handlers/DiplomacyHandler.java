package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
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
        this.populate();
    }

    private void        populate() {
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

    public Diplomacy                get(int id) { return diplomacies.get(id); }

    public List<Diplomacy>          get(City city) { return diplomacyMap.get(city); }

    public void                     delete(int id) {
        diplomacies.remove(id);
        diplomacyMap.values().stream().filter(d -> d.contains(get(id))).forEach(d -> d.remove(get(id)));
    }

    public List<City>               getEnemies(City city) {
        List<City>                  list = new ArrayList<>();

        for (Diplomacy d : diplomacies.values()) {
            if (!d.getRelation()) {
                if (d.getMain() == city)
                    list.add(d.getSub());
                else if (d.getSub() == city)
                    list.add(d.getMain());
            }
        }
        return list;
    }

    public List<City>               getAllies(City city) {
        List<City>                  list = new ArrayList<>();

        for (Diplomacy d : diplomacies.values()) {
            if (d.getRelation()) {
                if (d.getMain() == city)
                    list.add(d.getSub());
                else if (d.getSub() == city)
                    list.add(d.getMain());
            }
        }
        return list;
    }
}