package fr.AleksGirardey.Handlers;


import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerHandler {
    private Logger logger;

    private Map<String, DBPlayer>       players;
    private Map<User, DBPlayer>         playersMap;



    public PlayerHandler(Logger logger) {
        this.logger = logger;
        this.populate();
    }

    private void        populate() {
        String          sql = "SELECT * FROM `" + GlobalPlayer.tableName + "`;";
        DBPlayer        player;

        try {
            Statement   statement = new Statement(sql);
            statement.Execute();
            while (statement.getResult().next()) {
                player = new DBPlayer(statement.getResult());
                this.players.put(player.getDisplayName(), player);
                this.playersMap.put(player.getUser(), player);
            }
            statement.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Logger getLogger() { return logger; }

    public DBPlayer     get(Player player) {
        return this.get(player.getUniqueId().toString());
    }

    public DBPlayer     get(String uuid) {
        return this.playersMap.get(
                getUser(UUID.fromString(uuid)).get());
    }

    public DBPlayer     getFromName(String name) {
        return players.get(name);
    }

    public Optional<User>       getUser(UUID uuid) {
        Optional<UserStorageService>    userStorageService =
                Sponge.getServiceManager().provide(UserStorageService.class);
        return userStorageService.get().get(uuid);
    }

    public void         add(Player player) {
        DBPlayer        p = new DBPlayer(player);

        this.players.put(p.getDisplayName(), p);
        this.playersMap.put(p.getUser(), p);
    }

    public boolean exists(DBPlayer player) { return this.players.containsValue(player); }
}
