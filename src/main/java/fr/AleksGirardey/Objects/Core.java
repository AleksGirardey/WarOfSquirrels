package fr.AleksGirardey.Objects;

import com.google.inject.Inject;
import fr.AleksGirardey.Handlers.*;
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.List;

public class Core {

    @Inject
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
        logger.info("Core initialization...");
        try {
            database = new DatabaseHandler(logger);
        } catch (Exception e) {
            e.printStackTrace();
        }
        plugin = game;
        _main = main;
        playerHandler = new PlayerHandler(logger);
        cityHandler = new CityHandler(logger);
        chunkHandler = new ChunkHandler();
        permissionHandler = new PermissionHandler(logger);
        broadcastHandler = new BroadcastHandler();
        invitationHandler = new InvitationHandler();
        warHandler = new WarHandler();
        partyHandler = new PartyHandler();
    }

    public void close() throws SQLException {
        database.close();
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
}
