package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Handler<T> {
    protected static String DirName = "/WorldData";
    protected static String JsonName = "/ChunkHandler.json";
    protected String PrefixLogger;

    protected Logger Logger;

    protected List<T> dataArray;

    private File configFile;

    protected Handler( String prefix, Logger logger) {
        PrefixLogger = prefix;
        Logger = logger;
        dataArray = Collections.emptyList();
    }

    protected boolean Init() {
        String errorMessage = MessageFormat.format("{0} Couldn't create Json config at '{1}'",
                PrefixLogger, getConfigPath());
        configFile = new File(getConfigPath());

        try {
            if (!configFile.exists() && !configFile.createNewFile()) {
                Logger.error(errorMessage);
                return false;
            }
        } catch (IOException e) {
            Logger.error(errorMessage);
            return false;
        }
        return true;
    }

    protected void Save(Collection<T> data) {
        dataArray = new ArrayList<>(data);
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(configFile, dataArray);
        } catch (IOException e) {
            Logger.error(MessageFormat.format("{0} Couldn't save data to Json", PrefixLogger));
        }
    }

    protected abstract boolean Populate();

    protected boolean Load(TypeReference<List<T>> typeReference) {
        String errorMessage = MessageFormat.format("{0} Couldn't load Json data.", PrefixLogger);
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (new BufferedReader(new FileReader(getConfigPath())).readLine() != null) {
                dataArray = mapper.readValue(configFile, typeReference);
                if (!Populate()) {
                    Logger.error(errorMessage + " (Populate)");
                    return false;
                }
            }
        } catch (IOException e) {
            Logger.error(errorMessage + " (File creation)");
            return false;
        }
        return true;
    }

    public abstract boolean Delete(T value);

    public abstract void Log();

    public static String getConfigDir() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName;
    }

    protected static String getConfigPath() {
        return WarOfSquirrels.warOfSquirrelsConfigDir + DirName + JsonName;
    }
}
