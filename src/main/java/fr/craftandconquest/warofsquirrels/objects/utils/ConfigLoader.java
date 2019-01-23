package fr.craftandconquest.warofsquirrels.objects.utils;

import com.google.common.reflect.TypeToken;
import fr.craftandconquest.warofsquirrels.objects.Core;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigLoader.class);

    private ConfigurationLoader<CommentedConfigurationNode>             configManager;

    private ConfigurationNode                                           rootNode;
    
    private ConfigurationNode   distanceCities;
    private ConfigurationNode   distanceOutpost;
    private ConfigurationNode   shoutDistance;
    private ConfigurationNode   sayDistance;
    private ConfigurationNode   peaceTime;
    private ConfigurationNode   reincarnationTime;
    private ConfigurationNode   startBalance;
    private ConfigurationNode   preparationPhase;
    private ConfigurationNode   rollbackPhase;
    private ConfigurationNode   mapSize;
    private ConfigurationNode   territoriesGenerated;
    private ConfigurationNode   territorySize;
    private ConfigurationNode   territoryClaimLimit;

    private static final String DISTANCES = "Distances";
    private static final String WAR = "War";
    private static final String JOUEUR = "Joueur";
    private static final String TERRITORIES = "Territoires";

    public ConfigLoader(Path path) {
        try {
            configManager = HoconConfigurationLoader.builder().setPath(path).build();
            rootNode = configManager.load();

            if (rootNode.hasMapChildren())
                Core.getLogger().debug("ConfigLoader Map size : " + rootNode.getChildrenMap().size());
            if (rootNode.hasListChildren())
                Core.getLogger().debug("ConfigLoader List size : " + rootNode.getChildrenList().size());

            if (!rootNode.hasMapChildren()) {
                LOGGER.info("ConfigLoader default init");
                this.setDefaultConfig();
                configManager.save(rootNode);
            }
        } catch (IOException | ObjectMappingException e) {
            LOGGER.warn("ConfigLoader init error : " + e);
        }

        /* Distances */
        distanceCities = rootNode.getNode(DISTANCES, "cities");
        distanceOutpost = rootNode.getNode(DISTANCES, "outpost");
        shoutDistance = rootNode.getNode(DISTANCES, "shout");
        sayDistance = rootNode.getNode(DISTANCES, "say");

        /* war */
        peaceTime = rootNode.getNode(WAR, "peace");
        preparationPhase = rootNode.getNode(WAR, "preparationSeconds");
        rollbackPhase = rootNode.getNode(WAR, "rollbackSeconds");

        /* Player */
        reincarnationTime = rootNode.getNode(JOUEUR, "reincarnation");
        startBalance = rootNode.getNode(JOUEUR, "startBalance");

        /* Territoires */
        mapSize = rootNode.getNode(TERRITORIES, "mapSize");
        territoriesGenerated = rootNode.getNode(TERRITORIES, "generated");
        territorySize = rootNode.getNode(TERRITORIES, "territorySize");
        territoryClaimLimit = rootNode.getNode(TERRITORIES, "territoryClaimLimit");
    }

    private void setDefaultConfig() throws ObjectMappingException {
        /* Distances */
        rootNode.getNode(DISTANCES, "cities").setValue(20);
        rootNode.getNode(DISTANCES, "outpost").setValue(10);
        rootNode.getNode(DISTANCES, "shout").setValue(50);
        rootNode.getNode(DISTANCES, "say").setValue(30);

        /* war */
        rootNode.getNode(WAR, "peace").setValue(true);
        rootNode.getNode(WAR, "preparationSeconds").setValue(TypeToken.of(Long.class), 120L);
        rootNode.getNode(WAR, "rollbackSeconds").setValue(TypeToken.of(Long.class), 60L);

        /* Player */
        rootNode.getNode(JOUEUR, "reincarnation").setValue(30);
        rootNode.getNode(JOUEUR, "startBalance").setValue(100);

        /* Territoires */
        rootNode.getNode(TERRITORIES, "mapSize").setValue(5120);
        rootNode.getNode(TERRITORIES, "generated").setValue(false);
        rootNode.getNode(TERRITORIES, "territorySize").setValue(256);
        rootNode.getNode(TERRITORIES, "territoryClaimLimit").setValue(1500);
    }

    public void      close() {
        try {
            configManager.save(rootNode);
        } catch (IOException e) {
            LOGGER.warn("Cannot close the configuration class");
        }
    }

    public int getDistanceCities() { return distanceCities.getInt(); }
    public int getDistanceOutpost() { return distanceOutpost.getInt(); }
    public int getShoutDistance() { return shoutDistance.getInt(); }
    public int getSayDistance() { return sayDistance.getInt(); }
    public boolean isTimeAtPeace() { return peaceTime.getBoolean(); }
    public int getReincarnationTime() { return reincarnationTime.getInt(); }
    public int getStartBalance() { return startBalance.getInt(); }
    public long getPreparationPhase() { return preparationPhase.getLong(); }
    public long getRollbackPhase() { return rollbackPhase.getLong(); }
    public int  getMapSize() { return mapSize.getInt(); }
    public boolean getTerritoriesGenerated() { return territoriesGenerated.getBoolean(); }
    public int getTerritorySize() { return territorySize.getInt(); }
    public int getTerritoryClaimLimit() { return territoryClaimLimit.getInt(); }

    private void setNode(ConfigurationNode node, Object value) {
        node.setValue(value);
        try {
            this.configManager.save(rootNode);
        } catch (IOException e) {
            LOGGER.warn("Cannot save new value for node : " + node.toString() + " error : " + e);
        }
    }

    public void setDistanceCities(int distanceCities) {
        this.setNode(this.distanceCities, distanceCities);
    }

    public void setDistanceOutpost(int distanceOutpost) {
        this.setNode(this.distanceOutpost, distanceOutpost);
    }

    public void setShoutDistance(int shoutDistance) {
        this.setNode(this.shoutDistance, shoutDistance);
    }

    public void setSayDistance(int sayDistance) {
        this.setNode(this.sayDistance, sayDistance);
    }

    public void      setPeaceTime(Boolean peace) {
        this.setNode(this.peaceTime, peace);
    }

    public void setReincarnationTime(int reincarnationTime) {
        this.setNode(this.reincarnationTime, reincarnationTime);
    }

    public void setStartBalance(int startBalance) {
        this.setNode(this.startBalance, startBalance);
    }

    public void setPreparationPhase(long preparationPhase) {
        this.setNode(this.preparationPhase, preparationPhase);
    }

    public void setRollbackPhase(long rollbackPhase) {
        this.setNode(this.rollbackPhase, rollbackPhase);
    }

    public void setTerritoriesGenerated(boolean territoriesGenerated) {
        this.setNode(this.territoriesGenerated, territoriesGenerated);
    }
}
