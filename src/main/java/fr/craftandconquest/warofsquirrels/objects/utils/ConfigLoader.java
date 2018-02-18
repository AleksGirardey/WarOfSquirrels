package fr.craftandconquest.warofsquirrels.objects.utils;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

import java.io.IOException;

public class ConfigLoader {

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

    public ConfigLoader(ConfigurationLoader<CommentedConfigurationNode> configManager) {

        manager = configManager;
        try {
            rootNode = manager.load();

            if (!rootNode.hasListChildren()) {
                this.setDefaultConfig();
                rootNode = manager.load();
            }
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }

        /* Distances */
        distanceCities = rootNode.getNode("Distances", "cities");
        distanceOutpost = rootNode.getNode("Distances", "outpost");
        shoutDistance = rootNode.getNode("Distances", "shout");
        sayDistance = rootNode.getNode("Distances", "say");

        /* war */
        peaceTime = rootNode.getNode("war", "peace");
        preparationPhase = rootNode.getNode("war", "preparationSeconds");
        rollbackPhase = rootNode.getNode("war", "rollbackSeconds");

        /* Player */
        reincarnationTime = rootNode.getNode("Joueur", "reincarnation");
        startBalance = rootNode.getNode("Joueur", "startBalance");
    }

    private void setDefaultConfig() throws ObjectMappingException, IOException {
        /* Distances */
        rootNode.getNode("Distances", "cities").setValue(20);
        rootNode.getNode("Distances", "outpost").setValue(20);
        rootNode.getNode("Distances", "shout").setValue(50);
        rootNode.getNode("Distances", "say").setValue(30);

        /* war */
        rootNode.getNode("war", "peace").setValue(true);
        rootNode.getNode("war", "preparationSeconds").setValue(TypeToken.of(Long.class), 120L);
        rootNode.getNode("war", "rollbackSeconds").setValue(TypeToken.of(Long.class), 60L);

        /* Player */
        rootNode.getNode("Joueur", "reincarnation").setValue(30);
        rootNode.getNode("Joueur", "startBalance").getValue(100);

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
}
