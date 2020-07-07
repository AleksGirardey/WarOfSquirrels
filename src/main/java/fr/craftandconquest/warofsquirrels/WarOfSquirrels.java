package fr.craftandconquest.warofsquirrels;

import fr.craftandconquest.warofsquirrels.commands.CityCommand;
import fr.craftandconquest.warofsquirrels.events.PlayersInteractionHandler;
import fr.craftandconquest.warofsquirrels.events.WorldInteractionHandler;
import fr.craftandconquest.warofsquirrels.handler.*;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.Config;
import lombok.Getter;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WarOfSquirrels.warOfSquirrelsModId)
public class WarOfSquirrels {
    public static WarOfSquirrels instance;

    public static MinecraftServer server;

    public static final String warOfSquirrelsModId = "wos";
    public static final String warOfSquirrelsConfigDir = "./WarOfSquirrels";

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter private ChunkHandler chunkHandler;
    @Getter private PermissionHandler permissionHandler;
    @Getter private BroadCastHandler broadCastHandler;
    @Getter private CityHandler cityHandler;
    @Getter private FactionHandler factionHandler;
    @Getter private DiplomacyHandler diplomacyHandler;
    @Getter private PlayerHandler playerHandler;
    @Getter private PartyHandler partyHandler;
    @Getter private InfluenceHandler influenceHandler;
    @Getter private TerritoryHandler territoryHandler;
    @Getter private InvitationHandler invitationHandler;
    @Getter private WarHandler warHandler;

    public Config config;

    public WarOfSquirrels() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(new WorldInteractionHandler(LOGGER));
        MinecraftForge.EVENT_BUS.register(new PlayersInteractionHandler());
        MinecraftForge.EVENT_BUS.register(this);

        instance = this;
    }

    private void setup(final FMLCommonSetupEvent event) { }

    @SubscribeEvent
    public void OnServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("[WoS] Server Starting . . .");

        server = event.getServer();

        config = new Config("[WoS][Config]", LOGGER);

        permissionHandler = new PermissionHandler();
        playerHandler = new PlayerHandler(LOGGER);
        chunkHandler = new ChunkHandler(LOGGER);
        cityHandler = new CityHandler(LOGGER);
        territoryHandler = new TerritoryHandler(LOGGER);
        broadCastHandler = new BroadCastHandler(LOGGER);
        invitationHandler = new InvitationHandler(LOGGER);
        warHandler = new WarHandler();
        partyHandler = new PartyHandler();
//        cuboHandler = new CuboHandler(logger);
        diplomacyHandler = new DiplomacyHandler(LOGGER);
//        shopHandler = new ShopHandler(logger);
//        loanHandler = new LoanHandler(logger);
        factionHandler = new FactionHandler(LOGGER);
        influenceHandler = new InfluenceHandler(LOGGER);
//        updateHandler = new UpdateHandler();

        CityCommand.register(event.getCommandDispatcher());

        playerHandler.updateDependencies();

        LOGGER.info("[WoS] Server Started !");
    }

    public ConfigData getConfig() {
        return config.getConfiguration();
    }

    public void spreadPermissionDelete(IPermission target) {
        chunkHandler.spreadPermissionDelete(target);
        cityHandler.spreadPermissionDelete(target);
        //cuboHandler.spreadPermissionDelete(target);
    }
}
