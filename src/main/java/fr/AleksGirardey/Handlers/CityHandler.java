package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityHandler {

    private Logger  logger;
    private Map<Integer, City>      cities = new HashMap<>();

    @Inject
    public CityHandler(Logger logger) {
        this.logger = logger;
    }

    private Logger getLogger() {
        return logger;
    }

    public void        populate() {
        String          sql = "SELECT * FROM `" + GlobalCity.tableName + "`";
        City            city;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                city = new City(statement.getResult());
                this.cities.put(city.getId(), city);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public City             get(int id) { return cities.get(id); }

    public City             get(String name) {
        for (City city : cities.values())
            if (city.getDisplayName().equals(name))
                return city;
        return null;
    }

    public City             add(DBPlayer player, String displayName, Faction faction) {
        City                newOne = new City(
                displayName,
                player,
                faction,
                Core.getPermissionHandler().add(false, false, true),
                Core.getPermissionHandler().add(true, true, true),
                Core.getPermissionHandler().add(false, false, true),
                Core.getPermissionHandler().add(false, false, false),
                Core.getPermissionHandler().add(false, false, true));

        cities.put(newOne.getId(), newOne);
        return newOne;
    }

    public void                         delete(City city){
        cities.remove(city.getId());
        city.getOwner().setCity(null);
        Core.getInfoCityMap().remove(city);
        city.delete();
    }

    public Collection<DBPlayer>         getCitizens(City city) {
        return cities.get(city.getId()).getCitizens();
    }

    public List<String>                 getCitizensList(City city) {
        Collection<DBPlayer>            citizens = cities.get(city.getId()).getCitizens();
        List<String>                    res = new ArrayList<>();

        for (DBPlayer p : citizens)
            res.add(p.getDisplayName());

        return res;
    }

    public List<String>             getAssistants(int id) {
        Collection<DBPlayer>        citizens = cities.get(id).getCitizens();
        List<String>                res = new ArrayList<>();

        for (DBPlayer p : citizens)
            if (p.isAssistant())
                res.add(p.getDisplayName());

        return res;
    }

    public boolean      isLimitReached(City city) {
        return Core.getInfoCityMap().get(city).getCityRank().getCitizensMax() >= city.getCitizens().size();
    }

    public void         newCitizen(DBPlayer player, City city) {
        player.setCity(city);
        city.addCitizen(player);
        Core.getBroadcastHandler().cityChannel(city, player.getDisplayName() + " has join the city");
    }

    public void         removeCitizen(DBPlayer player) {
        player.getCity().removeCitizen(player);
        player.setCity(null);
    }

    public List<String>     getCityNameList() {
        return cities.values().stream().map(City::getDisplayName).collect(Collectors.toList());
    }

    public List<DBPlayer>       getOnlineDBPlayers(City city) {
        Collection<DBPlayer>    players = city.getCitizens();

        return players.stream().filter(p -> p.getUser().isOnline()).collect(Collectors.toList());
    }

    public List<Player>         getOnlinePlayers(City city) {
        Collection<DBPlayer>    players = city.getCitizens();

        return players.stream().filter(p -> p.getUser().isOnline()).map(p -> p.getUser().getPlayer().get()).collect(Collectors.toList());
    }

    public Map<City, InfoCity>   getCityMap() {
        Map<City, InfoCity>      map = new HashMap<>();

        for (City c : cities.values()) {
            logger.info("[InfoCity] new city info created for `" + c.getDisplayName() + "`.");
            map.put(c, new InfoCity(c));
        }
        return map;
    }
}