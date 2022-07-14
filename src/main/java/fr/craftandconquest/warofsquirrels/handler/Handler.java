package fr.craftandconquest.warofsquirrels.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.RegistryObject;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.utils.OnSaveListener;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public abstract class Handler<T extends RegistryObject> implements OnSaveListener {
    protected String PrefixLogger;
    protected Logger Logger;
    protected File configFile;

    protected List<T> dataArray;
    protected final Map<UUID, T> dataMap = new HashMap<>();

    protected Handler(String prefix, Logger logger) {
        PrefixLogger = prefix;
        Logger = logger;
        dataArray = new ArrayList<>();
        if (!Setup()) System.exit(-1);

        if (!Init()) return;
        if (!Load()) return;

        Log();
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

    @Override
    public void Save() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(configFile, dataArray);
        } catch (IOException e) {
            Logger.error(MessageFormat.format("{0} Couldn't save data to Json", PrefixLogger));
            return;
        }

        Logger.info(MessageFormat.format("{0} Saved {1} entries !", PrefixLogger, dataArray.size()));
    }

    protected boolean Populate() {
        dataArray.iterator().forEachRemaining(this::add);
        return true;
    }

    protected boolean add(T value) {
        boolean added = false;

        if (!dataArray.contains(value)) {
            dataArray.add(value);
            added = true;
        }

        if (!dataMap.containsKey(value.getUuid())) {
            dataMap.put(value.getUuid(), value);
            added = true;
        }

        if (added)
            Save();

        return added;
    }

    public List<T> getAll() {
        return dataArray;
    }

    protected boolean Load() {
        String errorMessage = MessageFormat.format("{0} Couldn't load Json data.", PrefixLogger);
        ObjectMapper mapper = new ObjectMapper();

        try {
            if (new BufferedReader(new FileReader(getConfigPath())).readLine() != null) {
                dataArray = mapper.readValue(configFile, new TypeReference<List<T>>(){});
                if (!Populate()) {
                    Logger.error(errorMessage + " (Populate)");
                    return false;
                }
            }
        } catch (IOException e) {
            Logger.error(errorMessage + "'" + getConfigPath() + "' (File creation) - " + e.getMessage());
            return false;
        }
        return true;
    }

    public void updateDependencies() {
        dataArray.forEach(RegistryObject::updateDependencies);
    }

    public T get(UUID uuid) {
        return dataMap.getOrDefault(uuid, null);
    }
    public T get(String name) { return dataArray.stream().filter(value -> value.getDisplayName().equals(name)).findFirst().orElse(null); }

    public boolean Delete(T value) {
        return Delete(value, false);
    }
    public boolean Delete(T value, boolean save) {
        dataMap.remove(value.getUuid());
        dataArray.remove(value);

        if (save)
            Save();

        return true;
    }

    public abstract void Log();

    protected String getJsonName() { return this.getClass().getSimpleName() + ".json"; }
    protected String getDirName() { return WarOfSquirrels.configDirName; }

    protected String getConfigDir() { return WarOfSquirrels.warOfSquirrelsConfigDir + "/" + getDirName(); }
    protected String getConfigPath() { return getConfigDir() + "/" + getJsonName(); }

    public abstract void spreadPermissionDelete(IPermission target);

    @Override
    public String Name() { return PrefixLogger; }
}
