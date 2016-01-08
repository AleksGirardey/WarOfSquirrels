package fr.AleksGirardey.Objects;

import com.flowpowered.math.vector.Vector3d;
import fr.AleksGirardey.Handlers.PlayerHandler;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {

    public static int getPlayerPos(Player player, String pos) {
        if (pos.equals("X"))
            return player.getLocation().getBlockX();
        else if (pos.equals("Y"))
            return player.getLocation().getBlockY();
        else
            return player.getLocation().getBlockZ();
    }

    public static String getListFromTableString(String[][] list, int index) {
        String res = "";
        int i = 0;

        while (i < list.length) {
            res += list[i][index];
            if (i != list.length - 1)
                res += ", ";
            i++;
        }
        return (res);
    }

    public static boolean checkCityName(String name) throws SQLException {
        Connection c = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        if (!name.matches("[A-Za-z0-9]+"))
            return (false);
        try {
            String sql = "SELECT `city_displayName` FROM `City` WHERE `city_displayName` = ?;";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setString(1, name);
            rs = statement.executeQuery();
            if (rs.first())
                return (false);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (true);
    }

    public static Location<World> getNearestSpawn(Player player) throws SQLException {
        PlayerHandler plh = Core.getPlayerHandler();
        Connection c = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        Location<World> pLocation = player.getLocation(), save = null;
        Vector3d chunk = null;

        try {
            String sql = "SELECT `chunk_respawnX`, `chunk_respawnY`, `chunk_respawnZ` FROM `Chunk` WHERE `chunk_cityId` = ? AND (`chunk_homeblock` = TRUE OR `chunk_outpost` = TRUE);";

            c = Core.getDatabaseHandler().getConnection();
            statement = c.prepareStatement(sql);
            statement.setInt(1, plh.getCity(player));
            rs = statement.executeQuery();
            while (rs.next()) {
                chunk = new Vector3d(rs.getDouble("chunk_respawnX"), rs.getDouble("chunk_respawnY"), rs.getDouble("chunk_respawnZ"));
                if (save == null || pLocation.getPosition().distance(chunk) < pLocation.getPosition().distance(save.getPosition())) {
                    save = player.getWorld().getLocation(chunk);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (statement != null) statement.close();
            if (c != null) c.close();
            if (rs != null) rs.close();
        }
        return (save);
    }
}
