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

    public static void          initCore(Logger logger, Game game) {
        logger.info("Core initialization...");
        try {
            database = new DatabaseHandler(logger);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin = game;
        logger.info("Creating Player Handler...");
        playerHandler = new PlayerHandler(logger);
        logger.info("Player Handler : OK\n" +
                "Creating City Handler...");
        cityHandler = new CityHandler(logger);
        logger.info("City Handler : OK\n" +
                "Creating Chunk Handler...");
        chunkHandler = new ChunkHandler();
        logger.info("Chunk Handler : OK\n" +
                "Creating Permission Handler...");
        permissionHandler = new PermissionHandler(logger);
        logger.info("Permission Handler : OK and Core is now ready to use.");
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

    public static void Send(String message) {
        plugin.getServer().getBroadcastChannel().send(Text.of(message));
    }
}
