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

    @Deprecated
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

    public static boolean checkCityName(String name) {
        Statement statement= null;
        String sql = "SELECT `city_displayName` FROM `City` WHERE `city_displayName` = ?;";

        if (!name.matches("[A-Za-z0-9]+") || name.length() > 34)
            return (false);
        try {
            statement = new Statement(sql);
            statement.getStatement().setString(1, name);
            if (statement.Execute().first())
                return (false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (true);
    }

    public static Location<World> getNearestSpawn(Player player) {
        PlayerHandler plh = Core.getPlayerHandler();
        Statement statement = null;

        Location<World> pLocation = player.getLocation(), save = null;
        Vector3d chunk = null;
        String sql = "SELECT `chunk_respawnX`, `chunk_respawnY`, `chunk_respawnZ` FROM `Chunk` WHERE `chunk_cityId` = ? AND (`chunk_homeblock` = TRUE OR `chunk_outpost` = TRUE);";

        try {
            statement = new Statement(sql);
            statement.getStatement().setInt(1, plh.<Integer>getElement(player, "player_cityId"));
            statement.Execute();
            while (statement.getResult().next()) {
                chunk = new Vector3d(
                        statement.getResult().getDouble("chunk_respawnX"),
                        statement.getResult().getDouble("chunk_respawnY"),
                        statement.getResult().getDouble("chunk_respawnZ"));
                if (save == null || pLocation.getPosition().distance(chunk) < pLocation.getPosition().distance(save.getPosition())) {
                    save = player.getWorld().getLocation(chunk);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (save);
    }
}
