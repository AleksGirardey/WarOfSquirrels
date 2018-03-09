package fr.craftandconquest.warofsquirrels.objects.utils;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Territory;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConfigLoader {

    @Inject
    @DefaultConfig(sharedRoot = true)
    private Path defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = true)
    private ConfigurationLoader<CommentedConfigurationNode>             configManager;



    private ConfigurationNode                                           rootNode;
    private ConfigurationLoader<CommentedConfigurationNode>             manager;
    
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
    
    private static final String DISTANCES = "Distances";
    private static final String WAR = "war";
    private static final String JOUEUR = "Joueur";
    private static final String TERRITORIES = "Territoires";

    public ConfigLoader() {
        try {
            rootNode = configManager.load();

            if (!rootNode.hasListChildren()) {
                this.setDefaultConfig();
                rootNode = manager.load();
            }
        } catch (IOException | ObjectMappingException e) {
            Core.getLogger().warn("ConfigLoader error " + e);
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
    }

    private void setDefaultConfig() throws ObjectMappingException, IOException {
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

        manager.save(rootNode);
    }

    public void      close() {
        try {
            manager.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void    set(ConfigurationNode node, Object value) {
        try {
            node.setValue(value);
            manager.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDistanceCities() { return distanceCities.getInt(); }

    public void setDistanceCities(int distanceCities) {
        this.set(this.distanceCities, distanceCities);
    }

    public int getDistanceOutpost() { return distanceOutpost.getInt(); }

    public void setDistanceOutpost(int distanceOutpost) {
        this.set(this.distanceOutpost, distanceOutpost);
    }

    public int getShoutDistance() { return shoutDistance.getInt(); }

    public void setShoutDistance(int shoutDistance) {
        this.set(this.shoutDistance, shoutDistance);
    }

    public int getSayDistance() { return sayDistance.getInt(); }

    public void setSayDistance(int sayDistance) {
        this.set(this.sayDistance, sayDistance);
    }

    public boolean isPeaceTime() { return peaceTime.getBoolean(); }

    public void      setPeaceTime(Boolean peace) {
        this.set(this.peaceTime, peace);
    }

    public int getReincarnationTime() { return reincarnationTime.getInt(); }

    public void setReincarnationTime(int reincarnationTime) { this.set(this.reincarnationTime, reincarnationTime); }

    public int getStartBalance() { return startBalance.getInt(); }

    public void setStartBalance(int startBalance) { this.set(this.startBalance, startBalance); }

    public long getPreparationPhase() { return preparationPhase.getLong(); }

    public void setPreparationPhase(long preparationPhase) { this.set(this.preparationPhase, preparationPhase); }

    public long getRollbackPhase() { return rollbackPhase.getLong(); }

    public void setRollbackPhase(long rollbackPhase) { this.set(this.rollbackPhase, rollbackPhase); }

    public int  getMapSize() { return mapSize.getInt(); }

    public boolean getTerritoriesGenerated() { return territoriesGenerated.getBoolean(); }

    public int getTerritorySize() { return territorySize.getInt(); }

    public void setTerritoriesGenerated(boolean territoriesGenerated) {
        this.set(this.territoriesGenerated, territoriesGenerated);
    }
}
