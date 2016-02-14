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

    public void            setPerm(int id, String perm, boolean value) {
        String          sql = "UPDATE `Permission` SET `Permission`.`" + perm + "` = ? WHERE `permission_id` = ?;";

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setBoolean(1, value);
            _statement.getStatement().setInt(2, id);
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
        int                 cityIdChunk, permId, cityIdPlayer = 0;

        cityIdChunk = chh.getCity(chunkId);
        permId = cih.<Integer>getElement(cityIdChunk, "city_permissionId");
        if (plh.<Integer>getElement(player, "player_cityId") != null)
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

    public String           getString(int cityId) {
        String              res = "";
        int                 permId = Core.getCityHandler().<Integer>getElement(cityId, "city_permissionId");

        res += "O [";
        res += (getPerm(permId, "permission_outsideBuild") ? "B" : "-");
        res += (getPerm(permId, "permission_outsideContainer") ? "C" : "-");
        res += (getPerm(permId, "permission_outsideSwitch") ? "S" : "-");
        res += "] | A [";
        res += (getPerm(permId, "permission_alliesBuild") ? "B" : "-");
        res += (getPerm(permId, "permission_alliesContainer") ? "C" : "-");
        res += (getPerm(permId, "permission_alliesSwitch") ? "S" : "-");
        res += "] | R [";
        res += (getPerm(permId, "permission_residentBuild") ? "B" : "-");
        res += (getPerm(permId, "permission_residentContainer") ? "C" : "-");
        res += (getPerm(permId, "permission_residentSwitch") ? "S" : "-");
        res += "]";

        return res;
    }

    private Logger getLogger() {
        return logger;
    }
}