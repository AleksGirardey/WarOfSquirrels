package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalDiplomacy;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;
import java.util.*;

public class CityHandler {

    private Logger  logger;

    @Inject
    public CityHandler(Logger logger) {
        this.logger = logger;
    }

    private Logger getLogger() {
        return logger;
    }

    public void             add(Player player, String displayName) {
        String          sql = "INSERT INTO `City` (`"
                + GlobalCity.displayName
                + "`, `"+ GlobalCity.tag
                + "`, `"+ GlobalCity.playerOwner
                + "`, `"+ GlobalCity.permRes
                + "`, `"+ GlobalCity.permAllies
                + "`, `"+ GlobalCity.permOutside
                + "`) VALUES (?, ?, ?, ?, ? , ?);";
        String                  substr = "";
        Statement               _statement = null;
        PermissionHandler       ph = Core.getPermissionHandler();

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setString(1, displayName);
            substr = displayName.length() <= 5 ? displayName : displayName.substring(0, 5);
            _statement.getStatement().setString(2, substr);
            _statement.getStatement().setString(3, player.getUniqueId().toString());
            _statement.getStatement().setInt(4, ph.add(true, true, true));
            _statement.getStatement().setInt(5, ph.add(false, false, true));
            _statement.getStatement().setInt(6, ph.add(false, false, false));
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void             delete(int id){
        String          sql = "DELETE FROM `City` WHERE `city_id` = ?;";
        Statement   _statement = null;

        try {
            Core.getChunkHandler().deleteCity(id);
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Update();
            _statement.Close();
            for (String assistant : this.getAssistants(id)) {
                Core.getPlayerHandler().setElement(Core.getPlayerHandler().getUuidFromName(assistant), GlobalPlayer.assistant, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> T            getElement(int id, String element){
        T       res = null;
        String  sql = "SELECT * FROM `City` WHERE `city_id` = ?;";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            if (_statement.Execute().next())
                res = (T) _statement.getResult().getObject(element);
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public <T> void         setElement(int id, String element, T value) {
        String              sql = "UPDATE `City` SET `City`.`" + element + "` = ? WHERE `city_id` = ?;";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setObject(1, value);
            _statement.getStatement().setInt(2, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int              getCityFromName(String name){
        int                 res = 0;
        String          sql = "SELECT `city_id` FROM `City` WHERE `city_displayName` = ?;";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setString(1, name);
            if (_statement.Execute().next())
                res = _statement.getResult().getInt("city_id");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public String[][]       getCitizens(int id) {
        String[][]              res = null;
        int                     i = 0, size = 0;
        String                  sql = "SELECT * FROM `Player` WHERE `player_cityId` = ?;";
        Statement               _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Execute().last();
            size = _statement.getResult().getRow();
            _statement.getResult().beforeFirst();
            res = new String[size][4];
            while (_statement.getResult().next()) {
                res[i][0] = _statement.getResult().getString("player_uuid");
                res[i][1] = _statement.getResult().getString("player_displayName");
                res[i][2] = _statement.getResult().getString("player_score");
                res[i][3] = _statement.getResult().getString("player_cityId");
                i++;
            }
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public List<String>     getCitizensList(int id) {
        List<String>        citizens = new ArrayList<String>();
        String              sql = "SELECT `player_displayName` FROM `Player` WHERE `player_cityId` = ?;";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Execute();
            while (_statement.getResult().next())
                citizens.add(_statement.getResult().getString("player_displayName"));
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citizens;
    }

    public List<String>     getAssistants(int id) {
        int                 size = 0, i = 0;
        List<String>        res = new ArrayList<String>();
        String              sql = "SELECT `player_uuid` FROM `Player` WHERE `player_cityId` = ? AND `player_assistant` = ?;";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            _statement.getStatement().setBoolean(2, true);
            _statement.Execute();
            while (_statement.getResult().next())
                 res.add(_statement.getResult().getString("player_uuid"));
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    public Map<Integer, Pair<Integer, String>>         getDiplomacy(int id, boolean relation) {
        Map<Integer, Pair<Integer, String>>    resultat = new HashMap<>();
        String                  name, sql = "SELECT * FROM `City`,`Diplomacy` WHERE ((`City`.`city_id` = `Diplomacy`.`diplomacy_mainCityId` AND `Diplomacy`.`diplomacy_subCityId` = ?)" +
                "OR (`City`.`city_id` = `Diplomacy`.`diplomacy_subCityId` AND `Diplomacy`.`diplomacy_mainCityId` = ?)) AND `Diplomacy`.`diplomacy_relation` = ?;";
        Statement               _statement = null;
        int                     idPerm;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, id);
            _statement.getStatement().setInt(2, id);
            _statement.getStatement().setBoolean(3, relation);
            _statement.Execute();
            while (_statement.getResult().next()) {
                idPerm = _statement.getResult().getInt(GlobalDiplomacy.permId);
                if (idPerm == 0)
                    idPerm = _statement.getResult().getInt((relation ? GlobalCity.permAllies : GlobalCity.permOutside));
                resultat.put(_statement.getResult().getInt(GlobalCity.id), new Pair<>(
                        idPerm, _statement.getResult().getString(GlobalCity.displayName)));
            }
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultat;
    }

    public boolean  areAllies(int owner, int player) {
        String      sql = "SELECT `diplomacy_relation` FROM `Diplomacy` WHERE ((`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?)" +
                        "OR (`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?));";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, owner);
            _statement.getStatement().setInt(2, player);
            _statement.getStatement().setInt(3, player);
            _statement.getStatement().setInt(4, owner);
            if (_statement.Execute().next())
                return (_statement.getResult().getBoolean("diplomacy_relation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean  areEnemies(int cityA, int cityD) {
        String      sql = "SELECT `diplomacy_relation` FROM `Diplomacy` WHERE (`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?);";
        Statement   _statement;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, cityA);
            _statement.getStatement().setInt(2, cityD);
            _statement.getStatement().setInt(3, cityD);
            _statement.getStatement().setInt(4, cityA);
            if (_statement.Execute().next())
                return (!_statement.getResult().getBoolean("diplomacy_relation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean  exists(int owner, int player) {
        String      sql = "SELECT `diplomacy_relation` FROM `Diplomacy` WHERE ((`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?)" +
                "OR (`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?));";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, owner);
            _statement.getStatement().setInt(2, player);
            _statement.getStatement().setInt(3, player);
            _statement.getStatement().setInt(4, owner);
            if (_statement.Execute().next())
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
    ** TRUE => Allies
    ** FALSE => Enemies
    */

    public void         setDiplomacy(int senderId, int receiverId, boolean relation) {
        String          sql = "INSERT INTO `Diplomacy` (`diplomacy_mainCityId`, `diplomacy_subCityId`, `diplomacy_relation`, `diplomacy_permissionId`) VALUES (?, ?, ?, ?);";
        if (!relation && !areAllies(senderId, receiverId))
            setNeutral(senderId, receiverId);
        Statement   _statement;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, senderId);
            _statement.getStatement().setInt(2, receiverId);
            _statement.getStatement().setBoolean(3, relation);
            _statement.getStatement().setInt(4, Core.getPermissionHandler().add(false, false, true));
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void         setNeutral(int cityId1, int cityId2) {
        String  sql = "DELETE FROM `Diplomacy` WHERE ((`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?)" +
                "OR (`diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?));";
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setInt(1, cityId1);
            _statement.getStatement().setInt(2, cityId2);
            _statement.getStatement().setInt(3, cityId2);
            _statement.getStatement().setInt(4, cityId1);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void         setMayor(String s, int playerCityId) {
        String uuid = this.<String>getElement(playerCityId, "city_playerOwner");
        String newMayor = Core.getPlayerHandler().getUuidFromName(s);

        this.<String>setElement(playerCityId, "city_playerOwner", newMayor);
        Core.getPlayerHandler().<Boolean>setElement(uuid, "player_assistant", true);
        Core.getPlayerHandler().<Boolean>setElement(newMayor, "player_assistant", false);
    }

    public boolean      isLimitReached(int cityId) {
        return false;
    }

    public void newCitizen(Player player, int city) {
        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") == null
                && !isLimitReached(city)) {
            Core.getPlayerHandler().setElement(player, "player_cityId", city);
            Core.getBroadcastHandler().cityChannel(
                    city,
                    Core.getPlayerHandler().<String>getElement(player, "player_displayName") + " join the city");
        }
    }

    public List<String> getCityNameList() {
        String          sql = "SELECT `city_displayName` FROM `City`;";
        List<String>    cities = new ArrayList<String>();
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.Execute();
            while (_statement.getResult().next())
                cities.add(_statement.getResult().getString("city_displayName"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public List<Player>     getOnlinePlayers(int cityId) {
        Collection<Player>  onlines = Core.getPlugin().getServer().getOnlinePlayers();
        List<Player>        players = new ArrayList<>();

        for (Player p : onlines) {
            if (Core.getPlayerHandler().<Integer>getElement(p, "player_cityId") != null &&
                Core.getPlayerHandler().<Integer>getElement(p, "player_cityId") == cityId)
                players.add(p);
        }
        return players;
    }

    public List<Integer>    getEnemies(int cityId) {
        String          sql = "SELECT `city_id` FROM `City`;";
        List<Integer>   list = new ArrayList<>();

        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.Execute();
            while (_statement.getResult().next())
                if (areEnemies(cityId, _statement.getResult().getInt("city_id")))
                    list.add(_statement.getResult().getInt("city_id"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Integer>    getAllies(int cityId) {
        String              sql = "Select `" + GlobalCity.id +"` FROM `City`";
        List<Integer>       list = new ArrayList<>();
        int                 id = 0;
        Statement           _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.Execute();
            while (_statement.getResult().next()) {
                id = _statement.getResult().getInt(GlobalCity.id);
                if (areAllies(cityId, id))
                    list.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String>     getEnemiesName(int cityId) {
        String              sql = "SELECT * FROM `City`;";
        List<String>        list = new ArrayList<>();
        Statement   _statement = null;

        try {
            _statement = new Statement(sql);
            _statement.Execute();
            while (_statement.getResult().next())
                if (areEnemies(cityId, _statement.getResult().getInt("city_id")))
                    list.add(_statement.getResult().getString("city_displayName"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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