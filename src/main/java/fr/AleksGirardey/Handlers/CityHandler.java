package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.block.InteractBlockEvent;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CityHandler {

    private Logger  logger;
    private Map<Integer, City>      cities = new HashMap<>();

    @Inject
    public CityHandler(Logger logger) {
        this.logger = logger;
        this.populate();
    }

    private Logger getLogger() {
        return logger;
    }

    private void        populate() {
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

    public City             add(Player player, String displayName) {
        City                newOne = new City(
                displayName,
                Core.getPlayerHandler().get(player),
                Core.getPermissionHandler().add(true, true, true),
                Core.getPermissionHandler().add(false, false, true),
                Core.getPermissionHandler().add(false, false, false));

        cities.put(newOne.getId(), newOne);
        return newOne;
    }

    public void             delete(int id){ cities.remove(id); }

    public Collection<DBPlayer>       getCitizens(int id) {
        return cities.get(id).getCitizens();
    }

    public List<String>             getCitizensList(int id) {
        Collection<DBPlayer>        citizens = cities.get(id).getCitizens();
        List<String>                res = new ArrayList<>();

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

    public List<Diplomacy>          getDiplomacy(City city, boolean relation) {
        List<Diplomacy>             res = new ArrayList<>(),
                diplo = Core.getDiplomacyHandler().get(city);

        for (Diplomacy diplomacy : diplo)
            if (diplomacy.getRelation() == relation)
                res.add(diplomacy);
        return res;
    }

    public boolean              areAllies(City owner, City player) {
        List<Diplomacy>         A = this.getDiplomacy(owner, true),
                B = this.getDiplomacy(player, true);

        for (Diplomacy diplo : A)
            if (diplo.getSub() == player)
                return true;

        for (Diplomacy diplo : B)
            if (diplo.getSub() == owner)
                return true;
        return false;
    }

    public boolean              areEnemies(City owner, City player) {
        List<Diplomacy>         A = this.getDiplomacy(owner, false),
                B = this.getDiplomacy(player, false);

        for (Diplomacy diplo : A)
            if (diplo.getSub() == player)
                return true;

        for (Diplomacy diplo : B)
            if (diplo.getSub() == owner)
                return true;
        return false;
    }

    public boolean  exists(City owner, City player) {
        List<Diplomacy>         list = new ArrayList<>();

        list.addAll(Core.getDiplomacyHandler().get(owner));
        list.addAll(Core.getDiplomacyHandler().get(player));

        for (Diplomacy d : list)
            if ((d.getMain() == owner && d.getSub() == player)
                    || (d.getMain() == player && d.getSub() == owner))
                return (true);
        return false;
    }

    /*
    ** TRUE => Allies
    ** FALSE => Enemies
    */

    public void         setNeutral(City owner, City player) {
        List<Diplomacy>         list = new ArrayList<>();

        list.addAll(Core.getDiplomacyHandler().get(owner));
        list.addAll(Core.getDiplomacyHandler().get(player));

        for (Diplomacy d : list)
            if ((d.getMain() == owner && d.getSub() == player)
                    || (d.getMain() == player && d.getSub() == owner))
                Core.getDiplomacyHandler().delete(d.getId());
    }
/*
    public void         setMayor(String s, int playerCityId) {
        String uuid = this.<String>getElement(playerCityId, "city_playerOwner");
        String newMayor = Core.getPlayerHandler().getUuidFromName(s);

        this.<String>setElement(playerCityId, "city_playerOwner", newMayor);
        Core.getPlayerHandler().<Boolean>setElement(uuid, "player_assistant", true);
        Core.getPlayerHandler().<Boolean>setElement(newMayor, "player_assistant", false);
    } */

    public boolean      isLimitReached(int cityId) {
        return false;
    }
    /*
    public void         newCitizen(Player player, int city) {
        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") == null
                && !isLimitReached(city)) {
            Core.getPlayerHandler().setElement(player, "player_cityId", city);
            Core.getBroadcastHandler().cityChannel(
                    city,
                    Core.getPlayerHandler().<String>getElement(player, "player_displayName") + " join the city");
        }
    } */

    public List<String>     getCityNameList() {
        List<String>        list = new ArrayList<>();

        for (City city : cities.values())
            list.add(city.getDisplayName());

        return list;
    }

    public List<Player>         getOnlinePlayers(City city) {
        Collection<DBPlayer>    players = city.getCitizens();

        return players.stream().filter(p -> p.getUser().isOnline()).map(p -> p.getUser().getPlayer().get()).collect(Collectors.toList());
    }

    public List<String>     getEnemiesName(City city) {
        List<String>        list = new ArrayList<>();
        List<City>          cities = Core.getDiplomacyHandler().getEnemies(city);

        for(City c : cities)
            list.add(c.getDisplayName());

        return list;
    }

    public List<String>     getAlliesName(City city) {
        List<String>        list = new ArrayList<>();
        List<City>          cities = Core.getDiplomacyHandler().getAllies(city);

        for(City c : cities)
            list.add(c.getDisplayName());

        return list;
    }

    // Change later
    public Map<Integer, InfoCity>   getCityMap() {
        Map<Integer, InfoCity>      map = new HashMap<>();
        String                      sql = "SELECT * FROM `City`;";
        Statement                   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.Execute();
            while (_statement.getResult().next()) {
                Core.getLogger().info("[InfoCity] new city info created for '" + _statement.getResult().getString("city_displayName") + "'");
                map.put(_statement.getResult().getInt("city_id"),
                        new InfoCity(
                                _statement.getResult().getInt("city_id"),
                                _statement.getResult().getInt("city_rank")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}