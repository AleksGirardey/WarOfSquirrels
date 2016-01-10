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

public class PermissionHandler {

    private Logger logger;
    private Statement _statement;

    @Inject
    public PermissionHandler(Logger logger) {
        this.logger = logger;
        this._statement = new Statement();
    }

    public void             add(int id) {
        String          sql = "INSERT INTO `Permission`(`permission_id`) VALUES (?);";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Update();
            sql = "UPDATE `City` SET `city_permissionId` = ? WHERE `city_id` = ?;";
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            _statement.getStatement().setInt(2, id);
            _statement.getStatement().executeUpdate();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void             delete(int id) {
        String          sql = "DELETE FROM `Permission` WHERE `permission_id` = ?;";
        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private boolean         getPerm(int id, String perm) {
        boolean             res = false;
        String              sql = "SELECT * FROM `Permission` WHERE `permission_id` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setInt(1, id);
            if (_statement.Execute().next())
                res = _statement.getResult().getBoolean(perm);
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (res);
    }

    private void            setPerm(int id, String perm, boolean value) throws SQLException {
        String          sql = "UPDATE `Permission` SET ? = ? WHERE `permission_id` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setString(1, perm);
            _statement.getStatement().setBoolean(2, value);
            _statement.getStatement().setInt(3, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean          ableTo(Player player, int chunkId, String perm) throws SQLException {
        PlayerHandler       plh = Core.getPlayerHandler();
        ChunkHandler        chh = Core.getChunkHandler();
        CityHandler         cih = Core.getCityHandler();
        int                 cityIdChunk, permId, cityIdPlayer;

        cityIdChunk = chh.getCity(chunkId);
        permId = cih.<Integer>getElement(cityIdChunk, "city_permissionId");
        cityIdPlayer = plh.<Integer>getElement(player, "player_cityId");

        if (cityIdPlayer != 0) {
            if (cityIdPlayer == cityIdChunk) {
                if (cih.<String>getElement(
                        cityIdChunk,
                        "city_playerOwner"
                ).equals(player.getUniqueId().toString()))
                    return true;
                return (getPerm(permId, "permission_resident" + perm));
            } else if (cih.areAllies(cityIdChunk, cityIdPlayer))
                return (getPerm(permId, "permission_allies" + perm));
        }
        return getPerm(permId, "permission_outside" + perm);
    }

    private Logger getLogger() {
        return logger;
    }
}