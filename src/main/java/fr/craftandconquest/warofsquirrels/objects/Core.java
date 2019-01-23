package fr.craftandconquest.warofsquirrels.objects;

import com.google.inject.Inject;
import fr.craftandconquest.warofsquirrels.handlers.*;
import fr.craftandconquest.warofsquirrels.Main;
import fr.craftandconquest.warofsquirrels.objects.city.InfoCity;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.faction.InfoFaction;
import fr.craftandconquest.warofsquirrels.objects.utils.ConfigLoader;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.*;

public class Core {

    private static final Logger LOGGERS = LoggerFactory.getLogger(Core.class);

    @Inject
    private static Game                     plugin;
    private static Main                     main;
    private static World                    world;

    private static ConfigLoader             config;
    private static Logger                   logger;

    private static DatabaseHandler          database;
    private static PlayerHandler            playerHandler;
    private static CityHandler              cityHandler;
    private static ChunkHandler             chunkHandler;
    private static TerritoryHandler         territoryHandler;
    private static PermissionHandler        permissionHandler;
    private static BroadcastHandler         broadcastHandler;
    private static InvitationHandler        invitationHandler;
    private static WarHandler               warHandler;
    private static PartyHandler             partyHandler;
    private static CuboHandler              cuboHandler;
    private static DiplomacyHandler         diplomacyHandler;
    private static ShopHandler              shopHandler;
    private static LoanHandler              loanHandler;
    private static FactionHandler           factionHandler;
    private static InfluenceHandler         influenceHandler;
    private static UpdateHandler            updateHandler;

    private static Map<City, InfoCity>          infoCityMap;
    private static Map<Faction, InfoFaction>    infoFactionMap;

    private Core() {}

    public static void          initCore(
            Logger logger,
            Game game,
            Main main,
            Path configFile, Path configDir) {
        logger.info("Core initialization...");

        plugin = game;
        Core.main = main;
        Core.logger = logger;
        world = game.getServer().getWorld(game.getServer().getDefaultWorldName()).orElse(null);

        config = new ConfigLoader(configFile);

        try {
            database = new DatabaseHandler(configDir);
        } catch (Exception e) {
            LOGGERS.warn("Cannot create database : " + e);
        }

        logger.info("Setting up handlers..");
        permissionHandler = new PermissionHandler(logger);
        playerHandler = new PlayerHandler(logger);
        cityHandler = new CityHandler(logger);
        chunkHandler = new ChunkHandler(logger);
        territoryHandler = new TerritoryHandler(world);
        broadcastHandler = new BroadcastHandler();
        invitationHandler = new InvitationHandler();
        warHandler = new WarHandler();
        partyHandler = new PartyHandler();
        cuboHandler = new CuboHandler(logger);
        diplomacyHandler = new DiplomacyHandler(logger);
        shopHandler = new ShopHandler(logger);
        loanHandler = new LoanHandler(logger);
        factionHandler = new FactionHandler(logger);
        influenceHandler = new InfluenceHandler();
        updateHandler = new UpdateHandler();

        logger.info("Updating dependencies..");
        permissionHandler.populate();
        playerHandler.populate();
        factionHandler.populate();
        cityHandler.populate();
        chunkHandler.populate();
        territoryHandler.populate();
        cuboHandler.populate();
        diplomacyHandler.populate();
        shopHandler.populate();
        loanHandler.populate();
        influenceHandler.populate();
        factionHandler.updateDependencies();
        playerHandler.updateDependencies();
        cuboHandler.updateDependencies();
        infoCityMap = getCityHandler().getCityMap();
        infoFactionMap = getFactionHandler().getFactionMap();

        logger.info("Done.");
    }

    public static void close() {
        try {
            database.close();
        } catch (SQLException e) {
            LOGGERS.warn("Failed to close database");
        }
    }

    /*
    ** Server
    */

    public static Game                  getPlugin() { return plugin; }

    public static Main                  getMain() { return main; }

    public static Logger                getLogger() { return logger; }

    /*
    ** handlers
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

    public static TerritoryHandler      getTerritoryHandler() { return territoryHandler; }

    public static PermissionHandler     getPermissionHandler() { return permissionHandler; }

    public static BroadcastHandler      getBroadcastHandler() { return broadcastHandler; }

    public static InvitationHandler     getInvitationHandler() { return invitationHandler; }

    public static WarHandler            getWarHandler() { return warHandler; }

    public static PartyHandler          getPartyHandler() { return partyHandler; }

    public static void                  send(String message) {
        send(Text.of(message));
    }

    public static void                  send(Text message) {
        plugin.getServer().getBroadcastChannel().send(message);
    }

    public static void                  sendText(Text text) {
        plugin.getServer().getBroadcastChannel().send(text);
    }

    public static ConfigLoader          getConfig() { return config; }

    public static CuboHandler           getCuboHandler() { return cuboHandler; }

    public static DiplomacyHandler      getDiplomacyHandler() { return diplomacyHandler; }

    public static ShopHandler           getShopHandler() { return shopHandler; }

    public static LoanHandler           getLoanHandler() { return loanHandler; }

    public static FactionHandler        getFactionHandler() { return factionHandler; }

    public static InfluenceHandler      getInfluenceHandler() { return influenceHandler; }

    public static UpdateHandler         getUpdateHandler() { return updateHandler; }

    public static Map<City, InfoCity>           getInfoCityMap() { return infoCityMap; }

    public static Map<Faction, InfoFaction>     getInfoFactionMap() { return infoFactionMap; }

    public static World                         getWorld() { return world; }


    /*
    ** Clean DB
    */

    public static void                  clear() {
        Set<Faction>                    factions = Core.getFactionHandler().getFactionMap().keySet();
        Collection<DBPlayer>            players = Core.getPlayerHandler().getMap().values();

        factions.forEach(f -> Core.getFactionHandler().delete(f));
        players.forEach(p -> Core.getPlayerHandler().delete(p));
    }
}
