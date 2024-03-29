package fr.craftandconquest.warofsquirrels.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.config.ConfigData;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

public class Config {
    private final String PrefixLogger;

    private File configFile;

    private final Logger Logger;

    @Getter @Setter private ConfigData configuration;

    public Config(String prefix, Logger logger) {
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

    private boolean Init() {
        String errorMessage = MessageFormat.format("{0} Couldn't create Json config at '{1}'",
                PrefixLogger, getConfigPath());
        configFile = new File(getConfigPath());

        try {
            if (!configFile.exists()) {
                if (!configFile.createNewFile()) {
                    Logger.error(errorMessage);
                    return false;
                }
                configuration = defaultConfiguration();
                return Save();
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
                ReSpawnPoint.DEFAULT_SPAWN = new ReSpawnPoint(WarOfSquirrels.SPAWN, new BlockPos(configuration.getServerSpawn().x, configuration.getServerSpawn().y, configuration.getServerSpawn().z));
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
        Map<PermissionRelation, Permission> defaultPermissions = new HashMap<>();

        initRanks.put(0, new CityRank("Settlement", "Chief", "Assistant", 1, 0));
        initRanks.put(1, new CityRank("Colony", "Governor", "Assistant", 25, 0));
        initRanks.put(2, new CityRank("Hamlet", "Burgomaster", "Assistant", 68, 2));
        initRanks.put(3, new CityRank("Village", "Lord", "Assistant", 68, 6));
        initRanks.put(4, new CityRank("Town", "Mayor", "Assistant", 68, 15));

        defaultPermissions.put(PermissionRelation.ENEMY, new Permission("enemy", false, false, false, false, false));
        defaultPermissions.put(PermissionRelation.ALLY, new Permission("ally", false, false, true, false, false));
        defaultPermissions.put(PermissionRelation.FACTION, new Permission("faction", false, false, true, false, true));
        defaultPermissions.put(PermissionRelation.RESIDENT, new Permission("resident", true, true, true, true, true));
        defaultPermissions.put(PermissionRelation.RECRUIT, new Permission("recruit", false, false, true, false, false));
        defaultPermissions.put(PermissionRelation.OUTSIDER, new Permission("outsider", false, false, false, false, false));

        Permission defaultNaturePermission = new Permission(true, true, true, true, true);

        return new ConfigData(
                /* Claiming */
                4, // minPartySizeToCreateCity;
                8, // distanceCities;
                10, // distanceOutpost;

                /* Influence */
                1500, // baseInfluenceRequired;

                /* Speaking */
                15, // shoutDistance;
                10, // sayDistance;

                /* Scoring */
                18, // daysNeededToWinFactionScore;

                /* World Config */
                5120, // mapSize; // World border set to 5120 and center in -65 -65
                0,
                0,
                512, // territorySize;
                false, // territoriesGenerated;
                true, // peaceTime;
                10, // reincarnationTime;
                15, // invitationTime;
                1500, // startBalance;
                new Vector3(11 , 86, -2),

                /* War */
                180, // preparationPhase;
                120, // rollbackPhase;
                1000, // influenceMax;
                150, // AttackCost;

                /* Info */
                0,

                initRanks, defaultPermissions, defaultNaturePermission);
    }
}
