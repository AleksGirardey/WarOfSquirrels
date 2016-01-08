package fr.AleksGirardey.Handlers;


import java.sql.PreparedStatement;
import fr.AleksGirardey.Objects.Core;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class PlayerHandler {
    private Logger logger;

    public PlayerHandler(Logger logger) {
        this.logger = logger;
    }

    public static enum sql_values {PUUID, NAME, SCORE, CITY};

    public static HashMap<sql_values, String> sql_tables = new HashMap<sql_values, String>();

    static {
        sql_tables.put(sql_values.PUUID, "player_uuid");
        sql_tables.put(sql_values.NAME, "player_displayName");
        sql_tables.put(sql_values.SCORE, "player_score");
        sql_tables.put(sql_values.CITY, "player_cityId");
    }

    public boolean exists(Player player) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;
        boolean bool = false;

        try {
            String sql = "SELECT `player_displayName` FROM `Player` WHERE `player_uuid` = ?;";
            //String          awesome_sql = Utils.getExistsSql();
            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            bool = statement.executeQuery().first();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
        return (bool);
    }

    public void add(Player player) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO `Player` (`player_uuid`, `player_displayName`) values (?, ?);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, player.getName());
            statement.executeUpdate();
        } catch (SQLException exc) {
            exc.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }
/*
    public String get(Player player, String info) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String res = null;

        try {
            String sql = "SELECT * FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            rs = statement.executeQuery();
            rs.next();
            res = rs.getString(info);
            logger.info("GET '" + info + "' = '" + res + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }*/

    public String get(String uuid, String info) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String res = null;

        try {
            String sql = "SELECT * FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            rs.next();
            res = rs.getString(info);
            logger.info("GET '" + info + "' = '" + res + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String           getDisplayName(Player player) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet rs = null;
        String              res = null;

        try {
            String sql = "SELECT `player_displayName` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("player_displayName");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public int              getScore(Player player) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        int                 res = -1;

        try {
            String      sql = "SELECT `player_score` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getInt("player_score");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public int           getCity(Player player) throws SQLException{
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        int                 res = 0;

        try {
            String          sql = "SELECT `player_cityId` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, player.getUniqueId().toString());
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getInt("player_cityId");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public void setCity(Player player, String name) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;

        try {
            String sql = "UPDATE `Player` SET `player_cityId` = ? WHERE `player_uuid` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, Core.getCityHandler().getCityFromName(name));
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    /*public String           getDisplayName(String uuid) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String sql = "SELECT `player_displayName` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("player_displayName");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public int              getScore(String uuid) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        int                 res = -1;

        try {
            String      sql = "SELECT `player_score` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getInt("player_score");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }

    public String           getCity(String uuid) throws SQLException{
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        String              res = null;

        try {
            String          sql = "SELECT `player_cityId` FROM `Player` WHERE `player_uuid`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            if (rs.next())
                res = rs.getString("player_cityId");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (res);
    }*/

    public Boolean isOwner(Player player) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        String res = null;

        try {
            String sql = "SELECT `city_playerOwner` FROM `City` WHERE `city_id`= ? ;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, Core.getPlayerHandler().getCity(player));
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
        return res.equals(player.getUniqueId().toString());
    }
}
