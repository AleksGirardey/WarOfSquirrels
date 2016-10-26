package fr.AleksGirardey.Handlers;


import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.Database.Statement;
=======
import fr.AleksGirardey.Objects.Statement;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import java.sql.SQLException;

public class PlayerHandler {
    private Logger logger;
<<<<<<< HEAD

    public PlayerHandler(Logger logger) {
        this.logger = logger;
=======
    private Statement   _statement;

    public PlayerHandler(Logger logger) {
        this.logger = logger;
        this._statement = new Statement();
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    private Logger getLogger() { return logger; }

    public void add(Player player) {
        String sql = "INSERT INTO `Player` (`player_uuid`, `player_displayName`) values (?, ?);";
<<<<<<< HEAD
        Statement   _statement;

        try {
            _statement = new Statement(sql);
=======

        try {
            _statement.NewQuery(sql);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            _statement.getStatement().setString(1, player.getUniqueId().toString());
            _statement.getStatement().setString(2, player.getName());
            _statement.Update();
            _statement.Close();
        } catch (SQLException exc) {
            exc.printStackTrace();
        }
    }

    public <T> T        getElement(Player player, String element) {
        return this.<T>getElement(player.getUniqueId().toString(), element);
    }

    public <T> T        getElement(String uuid, String element) {
        String sql = "SELECT * FROM `Player` WHERE `player_uuid` = ?;";
        Object res = null;
        Statement   _statement;

        try {
            _statement = new Statement(sql);
            _statement.getStatement().setString(1, uuid);
            if (_statement.Execute().next())
                res = _statement.getResult().getObject(element);
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (T) res;
    }

    public <T> void     setElement(Player player, String element, T value) {
        this.<T>setElement(player.getUniqueId().toString(), element, value);
    }

    public <T> void     setElement(String uuid, String element, T value) {
        String          sql = "UPDATE `Player` SET `Player`.`" + element + "` = ? WHERE `player_uuid` = ?;";
<<<<<<< HEAD
        Statement   _statement;

        try {
            _statement = new Statement(sql);
=======

        try {
            _statement.NewQuery(sql);
            //_statement.getStatement().setObject(1, element);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            _statement.getStatement().setObject(1, value);
            _statement.getStatement().setString(2, uuid);
            _statement.Update();
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String   getUuidFromName(String name) {
        String      res = null, sql = "SELECT `player_uuid` FROM `Player` WHERE `player_displayName` = ?;";
<<<<<<< HEAD
        Statement   _statement;

        try {
            _statement = new Statement(sql);_statement.getStatement().setString(1, name);
=======

        try {
            _statement.NewQuery(sql);
            _statement.getStatement().setString(1, name);
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
            if (_statement.Execute().next())
                res = _statement.getResult().getString("player_uuid");
            _statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return res;
    }

    public Boolean isOwner(Player player){
        return (player.getUniqueId().toString().equals(
                Core.getCityHandler().<String>getElement(
                        this.<Integer>getElement(player, "player_cityId"),
                        "city_playerOwner")));
    }

    public boolean exists(Player player) {
        return (this.<String>getElement(player, "player_displayName") != null);
    }
}
