package fr.AleksGirardey.Objects.DBObject;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalFaction;
import fr.AleksGirardey.Objects.Database.Statement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Faction extends DBObject {
    private static String      fields = "`" + GlobalFaction.displayName
            + "`, `" + GlobalFaction.capital + "`";

    private String      displayName;
    private City        capital;

    // Extra fields

    private int         capitalId;

    private Map<String, City>   cities = new HashMap<>();

    public Faction(String _displayName, City _capital) {
        super(GlobalFaction.id, GlobalFaction.tableName, fields);

        this.displayName = _displayName;
        this.capital = _capital;
        this.add("'" + displayName + "', "
                + _capital.getId());
        writeLog();
    }

    public  Faction(ResultSet rs) throws SQLException {
        super(GlobalFaction.id, GlobalFaction.tableName, fields);

        this._primaryKeyValue = "" + rs.getInt(GlobalFaction.id);
        this.displayName = rs.getString(GlobalFaction.displayName);
        this.capital = null;
        this.capitalId = rs.getInt(GlobalFaction.capital);

        this.populate();
        writeLog();
    }

    private void    populate() {
        String      sql = "SELECT * FROM `" + GlobalCity.tableName + "` " +
                "WHERE `" + GlobalFaction.id + "` = " + this._primaryKeyValue;

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
    }

    @Override
    protected void writeLog() {
        Core.getLogger().info("[Faction] (" + fields + ") : #" + _primaryKeyValue
                + "," + displayName
                + "," + capital.getDisplayName());
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
        this.edit(GlobalFaction.capital, "" + capital.getId());
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
            message += c.getDisplayName();
            if (i < size - 1)
                message += ", ";
        }
        return message;
    }
}