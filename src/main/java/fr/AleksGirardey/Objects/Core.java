package fr.AleksGirardey.Objects;

import com.google.inject.Inject;
import fr.AleksGirardey.Handlers.*;
import fr.AleksGirardey.Main;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
=======
import fr.AleksGirardey.Objects.Invitations.Invitation;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
<<<<<<< HEAD
import java.util.Map;
=======
import java.util.List;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

public class Core {

    @Inject
<<<<<<< HEAD
    private static Game                     plugin;
    private static Main                     _main;

    private static ConfigLoader             _config;
    private static Logger                   _logger;

    private static DatabaseHandler          database;
    private static PlayerHandler            playerHandler;
    private static CityHandler              cityHandler;
    private static ChunkHandler             chunkHandler;
    private static PermissionHandler        permissionHandler;
    private static BroadcastHandler         broadcastHandler;
    private static InvitationHandler        invitationHandler;
    private static WarHandler               warHandler;
    private static PartyHandler             partyHandler;
    private static CuboHandler              cuboHandler;
    private static Map<Integer, InfoCity>   infoCityMap;

    public static void          initCore(
            Logger logger,
            Game game,
            Main main,
            ConfigurationLoader<CommentedConfigurationNode> configManager) {
=======
    static Game                 plugin;
    static Main                 _main;

    static DatabaseHandler      database;
    static PlayerHandler        playerHandler;
    static CityHandler          cityHandler;
    static ChunkHandler         chunkHandler;
    static PermissionHandler    permissionHandler;
    static BroadcastHandler     broadcastHandler;
    static InvitationHandler    invitationHandler;
    static WarHandler           warHandler;
    static PartyHandler         partyHandler;

    public static void          initCore(Logger logger, Game game, Main main) {
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        logger.info("Core initialization...");
        try {
            database = new DatabaseHandler(logger);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin = game;
        _main = main;
<<<<<<< HEAD
        _logger = logger;
        _config = new ConfigLoader(configManager);
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        playerHandler = new PlayerHandler(logger);
        cityHandler = new CityHandler(logger);
        chunkHandler = new ChunkHandler();
        permissionHandler = new PermissionHandler(logger);
        broadcastHandler = new BroadcastHandler();
        invitationHandler = new InvitationHandler();
        warHandler = new WarHandler();
        partyHandler = new PartyHandler();
<<<<<<< HEAD
        cuboHandler = new CuboHandler(logger);
        infoCityMap = getCityHandler().getCityMap();
    }

    public static void close() {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
=======
    }

    public void close() throws SQLException {
        database.close();
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    public static Game getPlugin() { return plugin; }

    public static Main getMain() { return _main; }

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

    public static InvitationHandler getInvitationHandler() { return invitationHandler; }

    public static WarHandler    getWarHandler() { return warHandler; }

    public static PartyHandler  getPartyHandler() { return partyHandler; }

    public static void Send(String message) {
        plugin.getServer().getBroadcastChannel().send(Text.of(message));
    }
<<<<<<< HEAD

    public static void SendText(Text text) {
        plugin.getServer().getBroadcastChannel().send(text);
    }

    public static Logger getLogger() { return _logger; }

    public static Map<Integer, InfoCity>    getInfoCityMap() { return infoCityMap; }

    public static ConfigLoader  getConfig() { return _config; }

    public static CuboHandler   getCuboHandler() { return cuboHandler; }
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
}
