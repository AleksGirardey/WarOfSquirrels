package fr.AleksGirardey.Handlers;

import com.google.inject.Inject;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPermission;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

=======
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import java.sql.SQLException;

public class PermissionHandler {

    private Logger logger;
<<<<<<< HEAD
=======
    private Statement _statement;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

    @Inject
    public PermissionHandler(Logger logger) {
        this.logger = logger;
<<<<<<< HEAD
    }

    public int              add(boolean build, boolean container, boolean swi) {
        String          sql = "INSERT INTO `Permission`(`permission_build`, `permission_container`, `permission_switch`) VALUES (?, ?, ?);";
        Statement       _statement;
        int             id = 0;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setBoolean(1, build);
            _statement.getStatement().setBoolean(2, container);
            _statement.getStatement().setBoolean(3, swi);
            _statement.Update();
            id = _statement.getKeys().getInt(1);
=======
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
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
<<<<<<< HEAD
        return id;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    public void             delete(int id) {
        String          sql = "DELETE FROM `Permission` WHERE `permission_id` = ?;";
<<<<<<< HEAD
        Statement       _statement;

        try {
            _statement = new Statement(sql);
=======
        try {
            _statement.NewQuery(sql);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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
<<<<<<< HEAD
        Statement       _statement;

        try {
            _statement = new Statement(sql);
=======

        try {
            _statement.NewQuery(sql);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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
<<<<<<< HEAD
        Statement       _statement;

        try {
            _statement = new Statement(sql);
=======

        try {
            _statement.NewQuery(sql);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            _statement.getStatement().setBoolean(1, value);
            _statement.getStatement().setInt(2, id);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean          ableTo(Player player, int chunkId, String perm) throws SQLException {
<<<<<<< HEAD
        PlayerHandler       plh = Core.getPlayerHandler();
        CityHandler         cityHandler = Core.getCityHandler();
        String              option = GlobalCity.permOutside;
        int                 cityPlayerId, permId, cityId;

        cityId = Core.getChunkHandler().getCity(chunkId);
        // PLAYER cherche à PERM sur le chunk CHUNKID

        // verification si Player possède une ville
        cityPlayerId = plh.<Integer>getElement(player, GlobalPlayer.cityId);
        if (cityPlayerId != 0) { // PLAYER possède une ville
            if (cityPlayerId == cityId) { // Si PLAYER est un citoyen
                if (cityHandler.<String>getElement(cityId, GlobalCity.playerOwner)
                        .equals(player.getUniqueId().toString()))
                    return true; // Owner => always OK
                option = GlobalCity.permRes;
            } else if (cityHandler.areAllies(cityId, cityPlayerId)) { // Villes alliés
                permId = cityHandler.getDiplomacy(cityId, true).get(cityPlayerId).getL();
                return getPerm(permId, "permission_" + perm);
            }
        }
        permId = cityHandler.<Integer>getElement(cityId, option);
        return getPerm(permId, "permission_" + perm);
=======
        PlayerHandler plh = Core.getPlayerHandler();
        ChunkHandler chh = Core.getChunkHandler();
        CityHandler cih = Core.getCityHandler();
        int cityIdChunk, permId, cityIdPlayer = 0;

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
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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