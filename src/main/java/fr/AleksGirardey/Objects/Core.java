package fr.AleksGirardey.Objects;

import com.google.inject.Inject;
import fr.AleksGirardey.Handlers.*;
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.Shop;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.Map;

public class Core {

    @Inject
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
    private static DiplomacyHandler         diplomacyHandler;
    private static ShopHandler              shopHandler;
    private static Map<City, InfoCity>      infoCityMap;

    public static void          initCore(
            Logger logger,
            Game game,
            Main main,
            ConfigurationLoader<CommentedConfigurationNode> configManager) {
        logger.info("Core initialization...");
        try {
            database = new DatabaseHandler(logger);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin = game;
        _main = main;
        _logger = logger;
        _config = new ConfigLoader(configManager);
        logger.info("Setting up handlers..");
        permissionHandler = new PermissionHandler(logger);
        playerHandler = new PlayerHandler(logger);
        cityHandler = new CityHandler(logger);
        chunkHandler = new ChunkHandler(logger);
        broadcastHandler = new BroadcastHandler();
        invitationHandler = new InvitationHandler();
        warHandler = new WarHandler(configManager);
        partyHandler = new PartyHandler();
        cuboHandler = new CuboHandler(logger);
        diplomacyHandler = new DiplomacyHandler(logger);
        shopHandler = new ShopHandler(logger);

        logger.info("Updating dependencies..");
        permissionHandler.populate();
        playerHandler.populate();
        cityHandler.populate();
        chunkHandler.populate();
        cuboHandler.populate();
        diplomacyHandler.populate();
        shopHandler.populate();
        playerHandler.updateDependencies();
        cuboHandler.updateDependencies();
        infoCityMap = getCityHandler().getCityMap();
        logger.info("Done.");
    }

    public static void close() {
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    ** Server
    */

    public static Game                  getPlugin() { return plugin; }

    public static Main                  getMain() { return _main; }

    public static Logger                getLogger() { return _logger; }

    /*
    ** Handlers
    */

    public static DatabaseHandler       getDatabaseHandler() {
        return database;
    }

    public static PlayerHandler         getPlayerHandler() { return playerHandler; }

    public static CityHandler           getCityHandler() {
        return cityHandler;
    }

    public static ChunkHandler          getChunkHandler() {
        return chunkHandler;
    }

    public static PermissionHandler     getPermissionHandler() { return permissionHandler; }

    public static BroadcastHandler      getBroadcastHandler() { return broadcastHandler; }

    public static InvitationHandler     getInvitationHandler() { return invitationHandler; }

    public static WarHandler            getWarHandler() { return warHandler; }

    public static PartyHandler          getPartyHandler() { return partyHandler; }

    public static void                  Send(String message) {
        plugin.getServer().getBroadcastChannel().send(Text.of(message));
    }

    public static void                  SendText(Text text) {
        plugin.getServer().getBroadcastChannel().send(text);
    }

    public static Map<City, InfoCity>   getInfoCityMap() { return infoCityMap; }

    public static ConfigLoader          getConfig() { return _config; }

    public static CuboHandler           getCuboHandler() { return cuboHandler; }

    public static DiplomacyHandler      getDiplomacyHandler() { return diplomacyHandler; }

    public static ShopHandler           getShopHandler() { return shopHandler; }
}
