package fr.craftandconquest.warofsquirrels.objects.dbobject;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalCity;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalFaction;
import fr.craftandconquest.warofsquirrels.objects.database.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Faction extends DBObject {
    private static String      fields = "`" + GlobalFaction.displayName
            + "`, `" + GlobalFaction.capital + "`";

    private String      displayName;
    private City        capital;

    // Extra fields

    private int         capitalId;

    private Map<String, City>   cities = new HashMap<>();

    public Faction(String displayName, City capital) {
        super(GlobalFaction.id, GlobalFaction.tableName, fields);

        this.displayName = displayName;
        this.capital = capital;
        this.add("'" + displayName + "', NULL");
        writeLog();
    }

    public  Faction(ResultSet rs) throws SQLException {
        super(GlobalFaction.id, GlobalFaction.tableName, fields);

        this._primaryKeyValue = "" + rs.getInt(GlobalFaction.id);
        this.displayName = rs.getString(GlobalFaction.displayName);
        this.capital = null;
        this.capitalId = rs.getInt(GlobalFaction.capital);

        writeLog();
    }

    private void    populate() {
        String      sql = "SELECT * FROM `" + GlobalCity.tableName + "` " +
                "WHERE `" + GlobalCity.faction + "` = " + this._primaryKeyValue;

        try {
            Statement statement = new Statement(sql);
            statement.Execute();

            while (statement.getResult().next()) {
                ResultSet rs = statement.getResult();
                cities.put(rs.getString(GlobalCity.displayName), Core.getCityHandler().get(rs.getInt(GlobalCity.id)));
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void     updateDependencies() {
        this.capital = Core.getCityHandler().get(this.capitalId);
        this.populate();
        Core.getLogger().info("[Updating] faction `" + displayName + "` now got as capital `" + capital.getDisplayName() + "`");
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[faction] (" + fields + ") : #" + _primaryKeyValue
                + "," + displayName
                + ", NULL");
    }

    public int                  getId() { return Integer.parseInt(_primaryKeyValue); }

    public String               getDisplayName() { return displayName; }

    public void                 setDisplayName(String displayName) {
        this.displayName = displayName;
        this.edit(GlobalFaction.displayName, "'" + displayName + "'");
    }

    public City                 getCapital() { return capital; }

    public void                 setCapital(City capital) {
        this.capital = capital;
        this.edit(GlobalFaction.capital, (capital == null ? "NULL" : "" + capital.getId()));
    }

    public Map<String, City>    getCities() { return cities; }

    public void                 addCity(City city) { this.cities.put(city.getDisplayName(), city); }

    public Integer              getRank() { return 0; }

    public int                  getSize() {
        int         i = 0;

        for (City city : cities.values())
            i += city.getCitizens().size();
        return i;
    }

    public String               getCitiesAsString() {
        String                  message = "";
        int                     i = 0, size = cities.size();

        for (City c : cities.values()) {
            if (c != capital) {
                message += c.getDisplayName();
                if (i < size - 1)
                    message += ", ";
            }
            i++;
        }
        return message;
    }
}