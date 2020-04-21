package fr.craftandconquest.warofsquirrels;

import fr.craftandconquest.warofsquirrels.handler.CityHandler;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastHandler;
import fr.craftandconquest.warofsquirrels.handler.ChunkHandler;
import fr.craftandconquest.warofsquirrels.handler.PermissionHandler;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Config;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mod(WarOfSquirrels.warOfSquirrelsModId)
public class WarOfSquirrels {
    public static WarOfSquirrels instance;

    public static final String warOfSquirrelsModId = "wos";
    public static final String warOfSquirrelsConfigDir = "./WarOfSquirrels";

    private static final Logger LOGGER = LogManager.getLogger();

    @Getter private ChunkHandler chunkHandler;
    @Getter private PermissionHandler permissionHandler;
    @Getter private BroadCastHandler broadCastHandler;
    @Getter private CityHandler cityHandler;

    public Config config;

    private static final List<String> configDirs;

    public WarOfSquirrels() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        MinecraftForge.EVENT_BUS.register(this);
        instance = this;
    }

    static {
        configDirs = new ArrayList<>();

        configDirs.add(ChunkHandler.getConfigDir());
        configDirs.add(CityHandler.getConfigDir());
    }

    private void setup(final FMLCommonSetupEvent event) {
        File file;

        for (String dir : configDirs) {
            file = new File(dir);
            if (!file.exists() && !file.mkdirs())
                LOGGER.error("[WoS][Main] Couldn't create mod directory '" + dir + "'");

        }
    }

    @SubscribeEvent
    public void OnServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("[WoS] Server Starting . . .");

        chunkHandler = new ChunkHandler(LOGGER);
        cityHandler = new CityHandler(LOGGER);
        permissionHandler = new PermissionHandler();

        LOGGER.info("[WoS] Server Started !");
    }

    public ConfigData getConfig() {
        return config.getConfiguration();
    }
}
