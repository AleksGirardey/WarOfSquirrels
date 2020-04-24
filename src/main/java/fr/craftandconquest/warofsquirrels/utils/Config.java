package fr.craftandconquest.warofsquirrels.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import fr.craftandconquest.warofsquirrels.object.city.CityRank;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class Config {
    public final static List<String> configDirs;

    static {
        configDirs = new ArrayList<>();

        configDirs.add(WarOfSquirrels.warOfSquirrelsConfigDir);
        configDirs.add(WarOfSquirrels.warOfSquirrelsConfigDir + "/");
    }

    private final String PrefixLogger;

    private File configFile;

    private final Logger Logger;

    @Getter @Setter private ConfigData configuration;

    public  Config(String prefix, Logger logger) {
        PrefixLogger = prefix;
        Logger = logger;
        Setup();
        if (!Init()) System.exit(-1);
        if (!Load()) System.exit(-1);
    }

    private void Setup() {
        String errorMessage = MessageFormat.format("{0} Couldn't create Json dirs at '{1}'",
                PrefixLogger, getConfigDir());
        File file = new File(getConfigDir());
        if (!file.exists() && !file.mkdirs()) {
            Logger.error(errorMessage);
        }
    }

    private boolean Init()  {
        String errorMessage = MessageFormat.format("{0} Couldn't create Json config at '{1}'",
                PrefixLogger, getConfigPath());
        configFile = new File(getConfigPath());

        try {
            if (!configFile.exists() && !configFile.createNewFile()) {
                Logger.error(errorMessage);
                return false;
            }
            configuration = defaultConfiguration();
            return Save();
        } catch (IOException e) {
            Logger.error(errorMessage);
            return false;
        }
    }

    public boolean Save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(configFile, configuration);
        } catch (IOException e) {
            Logger.error(MessageFormat.format(
                    "{0} Couldn't save data to Json : ",
                    PrefixLogger) +
                    Arrays.toString(e.getStackTrace()));
            return false;
        }
        return true;
    }

    public boolean Load() {
        String errorMessage = MessageFormat.format("{0} Couldn't load configuration from Json", PrefixLogger);
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (new BufferedReader(new FileReader(getConfigPath())).readLine() != null) {
                configuration = mapper.readValue(configFile, ConfigData.class);
            }
        } catch (IOException e) {
            Logger.error(MessageFormat.format("{0} : {1}", errorMessage, e.getMessage()));
            return false;
        }
        return true;
    }

    protected String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir;
    }

    protected static String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + "/wos.config";
    }

    private ConfigData defaultConfiguration() {
        Map<Integer, CityRank> initRanks = new HashMap<>();
        initRanks.put(0, new CityRank("Colonie", "Chef", "Assistant", 4, 2));
        initRanks.put(1, new CityRank("Village", "Maire", "Assistant", 9, 4));
        initRanks.put(2, new CityRank("Ville", "Bourgmestre", "Assistant", 15, 8));
        initRanks.put(3, new CityRank("Comté", "Comte", "Assistant", 22, 11));
        initRanks.put(4, new CityRank("Duché", "Duc", "Assistant", 30, 15));
        initRanks.put(5, new CityRank("Royaume", "Roi", "Assistant", 40, 20));
        initRanks.put(6, new CityRank("Empire", "Empereur", "Assistant", 50, 25));

        return new ConfigData(20, 20, 15, 10, 3, 10, 1500, 120, 120, 5120, 400, 256, 400, initRanks);
    }
}
