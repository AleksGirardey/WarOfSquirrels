package fr.craftandconquest.warofsquirrels.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.ConfigData;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;

public class Config {

    private final String PrefixLogger;

    private File configFile;

    private final Logger Logger;

    @Getter @Setter private ConfigData configuration;

    public  Config(String prefix, Logger logger) {
        PrefixLogger = prefix;
        Logger = logger;
        if (!Init()) System.exit(-1);
        if (!Load()) System.exit(-1);
    }

    private boolean Init()  {
        String errorMessage = MessageFormat.format("{0} Couldn't create Json config at '{1}'",
                PrefixLogger, getConfigPath());
        configFile = new File(getConfigPath());

        try {
            if (!configFile.exists() && !configFile.createNewFile()) {
                configuration = defaultConfiguration();
                Logger.error(errorMessage);
                return false;
            }
        } catch (IOException e) {
            Logger.error(errorMessage);
            return false;
        }
        return true;
    }

    public boolean Save() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(configFile, configuration);
        } catch (IOException e) {
            Logger.error(MessageFormat.format("{0} Couldn't save data to Json", PrefixLogger));
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

    protected static String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + "WoS.properties";
    }

    private ConfigData defaultConfiguration() {
        return new ConfigData(20, 20, 15, 10, 3, 10, 1500, 120, 120, 5120, 400, 256, 400);
    }
}
