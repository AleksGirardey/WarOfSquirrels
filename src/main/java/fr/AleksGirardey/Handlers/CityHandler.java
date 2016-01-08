package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityHandler {

    private Logger  logger;
    private Statement   _statement;

    @Inject
    public CityHandler(Logger logger) {
        this.logger = logger;
        this._statement = new Statement();
    }

    public void             add(Player player, String displayName) {
        String          sql = "INSERT INTO `City` (`city_displayName`, `city_tag`, `city_playerOwner`) VALUES (?, ?, ?);";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setString(1, displayName);
            _statement.getStatement().setString(2, displayName.substring(0, 5));
            _statement.getStatement().setString(3, player.getUniqueId().toString());
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void             delete(int id){
        String          sql = "DELETE FROM `City` WHERE `city_id` = ?;";
        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Update();
            _statement.Close();
            Core.getChunkHandler().deleteCity(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> T              getElement(int id, String element){
        T       res = null;
        String  sql = "SELECT * FROM `City` WHERE `city_id` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            if (_statement.Execute().next())
                res = (T) _statement.getResult().getObject(element);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public int           getCityFromName(String name) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        int                 res = 0;

        try {
            String          sql = "SELECT `city_id` FROM `City` WHERE `city_displayName` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, name);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getInt("city_id");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String[][]           getCitizens(int id) throws SQLException {
        Connection              c = null;
        PreparedStatement       statement = null;
        ResultSet               rs = null;
        String[][]              res = null;
        int                     i = 0, size = 0;

        try {
            String              sql = "SELECT * FROM `Player` WHERE `player_cityId` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            rs.last();
            size = rs.getRow();
            rs.beforeFirst();
            getLogger().error("[Get Citizens] rs size : " + size);
            res = new String[size][4];
            while (rs.next()) {
                res[i][0] = rs.getString("player_uuid");
                res[i][1] = rs.getString("player_displayName");
                res[i][2] = rs.getString("player_score");
                res[i][3] = rs.getString("player_cityId");
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public boolean  areAllies(int owner, int player) {
        String      sql = "SELECT `diplomacy_relation` FROM `Diplomacy` WHERE `diplomacy_mainCityId` = ? AND `diplomacy_subCityId` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, owner);
            _statement.getStatement().setInt(2, player);
            if (_statement.Execute().next())
                return (_statement.getResult().getBoolean("diplomacy_relation"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Logger getLogger() {
        return logger;
    }

/*    public String           getPerm(String id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String          sql = "SELECT `city_permissionId` FROM `City` WHERE `city_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, Integer.parseInt(id));
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("city_permissionId");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String           getDisplayName(int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String          sql = "SELECT `city_displayName` FROM `City` WHERE `city_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("city_displayName");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String           getTag(int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String          sql = "SELECT `city_tag` FROM `City` WHERE `city_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("city_tag");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String           getCityOwner(int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String          sql = "SELECT `city_playerOwner` FROM `City` WHERE `city_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("city_playerOwner");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }*/

    public void                 setAlly(int cityId1, int cityId2) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            DeleteDoubleDiplomacyLinks(cityId1, cityId2);

            String          sql = "INSERT INTO `Diplomacy` (`diplomacy_mainCityId`, `diplomacy_subCityId`, `diplomacy_relation`) VALUES (?, ?, 1);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public void                 setNeutral(int cityId1, int cityId2) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            DeleteDoubleDiplomacyLinks(cityId1, cityId2);

            String          sql = "INSERT INTO `Diplomacy` (`diplomacy_mainCityId`, `diplomacy_subCityId`, `diplomacy_relation`) VALUES (?, ?, 0);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public void                 setEnemy(int cityId1, int cityId2) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            DeleteDoubleDiplomacyLinks(cityId1, cityId2);

            String          sql = "INSERT INTO `Diplomacy` (`diplomacy_mainCityId`, `diplomacy_subCityId`, `diplomacy_relation`) VALUES (?, ?, 2);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    private void DeleteDoubleDiplomacyLinks(int cityId1, int cityId2) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;

        try {
            String          sql = "SELECT `diplomacy_id` FROM `Diplomacy` WHERE `diplomacy_mainCityId` = ? AND `diplomacy_subCItyId` = ?;",
                            remove = "DELETE FROM `Diplomacy` WHERE `diplomacy_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, cityId1);
            statement.setInt(2, cityId2);
            rs = statement.executeQuery();
            if (rs.next()) {
                statement = c.prepareStatement(remove);
                statement.setInt(1, rs.getInt("diplomacy_id"));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
    }
}