package fr.AleksGirardey.Handlers;


import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Database.Statement;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.user.UserStorageService;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class        PlayerHandler {
    private Logger  logger;

    private Map<String, DBPlayer>       players = new HashMap<>();
    private Map<User, DBPlayer>         playersMap = new HashMap<>();

    private Map<DBPlayer, Task>         incarnationMap = new HashMap<>();

    public PlayerHandler(Logger logger) {
        this.logger = logger;
    }

    public void        populate() {
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

    public void         updateDependencies() {
        players.values().forEach(DBPlayer::updateDependencies);
    }

    private Logger      getLogger() { return logger; }

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

    public boolean      exists(DBPlayer player) { return this.players.containsValue(player); }

    private Task        newReincarnation(DBPlayer player) {
        Scheduler       scheduler = Core.getPlugin().getScheduler();
        Task.Builder    builder = scheduler.createTaskBuilder();

        return builder
                .execute(() -> Core.getPlayerHandler().cancelReincarnation(player))
                .delay(ConfigLoader.reincarnationTime, TimeUnit.SECONDS)
                .submit(Core.getMain());
    }

    private void         cancelReincarnation(DBPlayer player) {
        incarnationMap.remove(player);
        player.setReincarnation(false);
    }

    public void         setReincarnation(DBPlayer player) {
        if (incarnationMap.containsKey(player)) {
            incarnationMap.get(player).cancel();
            incarnationMap.remove(player);
        }
        player.setReincarnation(true);
        incarnationMap.put(player, newReincarnation(player));
    }

    public List<String> getStringList() {
        List<String>    list = new ArrayList<>();

        list.addAll(players.keySet());
        return list;
    }
}
