package fr.AleksGirardey.Objects;

import com.google.inject.Inject;
import fr.AleksGirardey.Handlers.*;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class Core {

    @Inject
    static Game                 plugin;

    static DatabaseHandler      database;
    static PlayerHandler        playerHandler;
    static CityHandler cityHandler;
    static ChunkHandler chunkHandler;
    static PermissionHandler permissionHandler;
    static BroadcastHandler broadcastHandler;

    public static void          initCore(Logger logger, Game game) {
        logger.info("Core initialization...");
        try {
            database = new DatabaseHandler(logger);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin = game;
        playerHandler = new PlayerHandler(logger);
        cityHandler = new CityHandler(logger);
        chunkHandler = new ChunkHandler();
        permissionHandler = new PermissionHandler(logger);
        broadcastHandler = new BroadcastHandler();
    }

    public void close() throws SQLException {
        database.close();
    }

    public static Game getPlugin() { return plugin; }

    public static DatabaseHandler getDatabaseHandler() {
        return database;
    }

    public static PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public static CityHandler getCityHandler() {
        return cityHandler;
    }

    public static ChunkHandler getChunkHandler() {
        return chunkHandler;
    }

    public static PermissionHandler getPermissionHandler() {
        return permissionHandler;
    }

    public static BroadcastHandler  getBroadcastHandler() { return broadcastHandler; }

    public static void Send(String message) {
        plugin.getServer().getBroadcastChannel().send(Text.of(message));
    }
}
