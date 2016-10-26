package fr.AleksGirardey.Objects.Utilitaires;

import fr.AleksGirardey.Objects.Core;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ConfigLoader {

    public static int                                                       distanceCities;
    public static int                                                       distanceOutpost;
    private static ConfigurationNode                                        rootNode;
    private static ConfigurationLoader<CommentedConfigurationNode>          manager;

    public ConfigLoader(ConfigurationLoader<CommentedConfigurationNode> configManager) {
        Path configPath = FileSystems.getDefault().getPath("WarOfSquirrels/config", "WOS.properties");

        try {
            if (!configPath.toFile().exists()) {
                File conf = configPath.toFile();
                if (!conf.createNewFile())
                    Core.getLogger().error("Can't create WOS.properties");
                ConfigurationLoader<CommentedConfigurationNode> defaultManager = HoconConfigurationLoader
                        .builder()
                        .setURL(getClass().getClassLoader().getResource("config/WOS.properties"))
                        .build();
                rootNode = defaultManager.load();
                manager = HoconConfigurationLoader.builder().setPath(configPath).build();
                manager.save(rootNode);
            } else
                manager = configManager;
            rootNode = manager.load();
            distanceCities = rootNode.getNode("Distances", "cities").getInt(20);
            distanceOutpost = rootNode.getNode("Distances", "outpost").getInt(20);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void      close() {
        try {
            manager.save(rootNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}