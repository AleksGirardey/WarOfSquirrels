package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
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
    protected String PrefixLogger;

    protected Logger Logger;

    protected List<T> dataArray;

    protected File configFile;

    protected Handler( String prefix, Logger logger) {
        PrefixLogger = prefix;
        Logger = logger;
        dataArray = Collections.emptyList();
        if (!Setup()) System.exit(-1);
    }

    protected boolean Setup() {
        File file = new File(getConfigDir());

        if (!file.exists() && !file.mkdirs())
            Logger.error("[WoS][Main] Couldn't create mod directory '" + getConfigDir() + "'");

        return true;
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

    public void Save() {
        Save(dataArray);
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

    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    protected abstract boolean add(T value);

    public List<T> getAll() { return dataArray; }

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
            Logger.error(errorMessage + "'" + getConfigPath() + "' (File creation)");
            return false;
        }
        return true;
    }

    public abstract boolean Delete(T value);

    public abstract void Log();

    public abstract String getConfigDir();

    protected abstract String getConfigPath();

    public abstract void spreadPermissionDelete(IPermission target);
}
