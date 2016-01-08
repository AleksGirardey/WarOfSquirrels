package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionHandler {

    private Logger logger;

    @Inject
    public PermissionHandler(Logger logger) {
        this.logger = logger;
    }

    public void             add(int id) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            String          sql = "INSERT INTO `Permission`(`permission_id`) VALUES (?);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            statement.executeUpdate();
            sql = "UPDATE `City` SET `city_permissionId` = ? WHERE `city_id` = ?;";
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    private boolean         getPerm(int id, String perm) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;
        ResultSet           rs = null;
        boolean             res = false;

        try {
            String          sql = "SELECT * FROM `Permission` WHERE `permission_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, id);
            rs = statement.executeQuery();
            rs.next();
            res = rs.getBoolean(perm);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
        return (res);
    }

    private void            setPerm(int id, String perm, boolean value) throws SQLException {
        Connection          c = null;
        PreparedStatement   statement = null;

        try {
            String          sql = "UPDATE `Permission` SET ? = ? WHERE `permission_id` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, perm);
            statement.setBoolean(2, value);
            statement.setInt(3, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
        }
    }

    public boolean          ableTo(Player player, String chunkId, String perm) throws SQLException {
        PlayerHandler       plh = Core.getPlayerHandler();
        ChunkHandler        chh = Core.getChunkHandler();
        CityHandler         cih = Core.getCityHandler();
        int                 chunk = Integer.parseInt(chunkId), permId;

        if (chunkId == null) {
            getLogger().info("CHUNKID IS NULL");
            return false;
        }
        else
            permId = Integer.parseInt(cih.getPerm(String.valueOf(chh.getCity(chunk))));

        if (plh.getCity(player) != 0) {
            if (plh.getCity(player) == chh.getCity(chunk)) {
                if (cih.getCityOwner(plh.getCity(player)).equals(player.getUniqueId().toString()))
                    return true;
                else
                    return (getPerm(permId,"permission_resident" + perm));
            }
            /* ADD ALLIES */
        }
        return getPerm(permId, "permission_outside" + perm);
    }

    private Logger getLogger() {
        return logger;
    }
}