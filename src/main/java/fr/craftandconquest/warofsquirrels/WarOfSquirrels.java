package fr.craftandconquest.warofsquirrels;

import fr.craftandconquest.warofsquirrels.commands.register.CommandRegisterManager;
import fr.craftandconquest.warofsquirrels.events.PlayersInteractionHandler;
import fr.craftandconquest.warofsquirrels.events.RespawnEvents;
import fr.craftandconquest.warofsquirrels.events.ServerEvents;
import fr.craftandconquest.warofsquirrels.events.WorldInteractionHandler;
import fr.craftandconquest.warofsquirrels.handler.*;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.object.config.ConfigData;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.BackUpUtils;
import fr.craftandconquest.warofsquirrels.utils.Config;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = WarOfSquirrels.warOfSquirrelsModId, bus = Mod.EventBusSubscriber.Bus.MOD)
@Mod(WarOfSquirrels.warOfSquirrelsModId)
public class WarOfSquirrels {
    public static WarOfSquirrels instance;

    public static MinecraftServer server;

    public static final String warOfSquirrelsModId = "wos";
    public static final String warOfSquirrelsConfigDir = "./WarOfSquirrels";

    public static final Logger LOGGER = LogManager.getLogger();

    private final CommandRegisterManager commandRegisterManager;

    private final static ResourceLocation SpawnDimension = new ResourceLocation("minecraft", "craftandconquest/spawn");
    public static final ResourceKey<Level> SPAWN = ResourceKey.create(Registry.DIMENSION_REGISTRY, SpawnDimension);
    @Getter
    private Boolean isModInit = false;

    @Getter
    private ChunkHandler chunkHandler;
    @Getter
    private PermissionHandler permissionHandler;
    @Getter
    private BroadCastHandler broadCastHandler;
    @Getter
    private CityHandler cityHandler;
    @Getter
    private FactionHandler factionHandler;
    @Getter
    private DiplomacyHandler diplomacyHandler;
    @Getter
    private PlayerHandler playerHandler;
    @Getter
    private PartyHandler partyHandler;
    @Getter
    private InfluenceHandler influenceHandler;
    @Getter
    private TerritoryHandler territoryHandler;
    @Getter
    private InvitationHandler invitationHandler;
    @Getter
    private WarHandler warHandler;
    @Getter
    private CuboHandler cuboHandler;
    @Getter
    private UpdateHandler updateHandler;
    @Getter private AdminHandler adminHandler;
    @Getter private BastionHandler bastionHandler;

    public Config config;

    public WarOfSquirrels() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        instance = this;

        commandRegisterManager = new CommandRegisterManager();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RespawnEvents());
        MinecraftForge.EVENT_BUS.register(new WorldInteractionHandler());
        MinecraftForge.EVENT_BUS.register(new ServerEvents(LOGGER));
        MinecraftForge.EVENT_BUS.register(new PlayersInteractionHandler());
    }

    private void setup(final FMLCommonSetupEvent event) { }

    @SubscribeEvent
    public void OnRegisterCommand(RegisterCommandsEvent event) {
        commandRegisterManager.register(event.getDispatcher());

        isModInit = true;

        LOGGER.info("[WoS] Commands registered ! ");
    }

    @SubscribeEvent
    public void OnServerStarting(ServerStartingEvent event) {
        LOGGER.info("[WoS] Server Starting . . .");
        server = event.getServer();
        config = new Config("[WoS][Config]", LOGGER);

        BackUpUtils.DoBackUp();

        updateHandler = new UpdateHandler(LOGGER);
        permissionHandler = new PermissionHandler();
        playerHandler = new PlayerHandler(LOGGER);
        chunkHandler = new ChunkHandler(LOGGER);
        cityHandler = new CityHandler(LOGGER);
        territoryHandler = new TerritoryHandler(LOGGER);
        invitationHandler = new InvitationHandler(LOGGER);
        warHandler = new WarHandler();
        partyHandler = new PartyHandler();
        cuboHandler = new CuboHandler(LOGGER);
        diplomacyHandler = new DiplomacyHandler(LOGGER);
//        shopHandler = new ShopHandler(logger);
//        loanHandler = new LoanHandler(logger);
        factionHandler = new FactionHandler(LOGGER);
        influenceHandler = new InfluenceHandler(LOGGER);
        broadCastHandler = new BroadCastHandler(LOGGER);
        adminHandler = new AdminHandler(LOGGER);
        bastionHandler = new BastionHandler(LOGGER);

        AddSaveListeners();

        factionHandler.updateDependencies();
        playerHandler.updateDependencies();
        cuboHandler.updateDependencies();
        cityHandler.updateDependencies();
        bastionHandler.updateDependencies();
        chunkHandler.updateDependencies();
        territoryHandler.updateDependencies();
        influenceHandler.updateDependencies();
        diplomacyHandler.updateDependencies();

        LOGGER.info("[WoS] Handlers created ! ");
    }

    public ConfigData getConfig() {
        return config.getConfiguration();
    }

    public void spreadPermissionDelete(IPermission target) {
        chunkHandler.spreadPermissionDelete(target);
        cityHandler.spreadPermissionDelete(target);
        cuboHandler.spreadPermissionDelete(target);
    }

    public void AddSaveListeners() {
        UpdateHandler.OnSaveUpdate.add(playerHandler);
        UpdateHandler.OnSaveUpdate.add(chunkHandler);
        UpdateHandler.OnSaveUpdate.add(cityHandler);
        UpdateHandler.OnSaveUpdate.add(cuboHandler);
        UpdateHandler.OnSaveUpdate.add(diplomacyHandler);
        UpdateHandler.OnSaveUpdate.add(factionHandler);
        UpdateHandler.OnSaveUpdate.add(influenceHandler);
        UpdateHandler.OnSaveUpdate.add(territoryHandler);
        UpdateHandler.OnSaveUpdate.add(bastionHandler);
    }

    public void debugLog(String message) {
        LOGGER.info("[WoS][Debug] " + message);
    }
}
