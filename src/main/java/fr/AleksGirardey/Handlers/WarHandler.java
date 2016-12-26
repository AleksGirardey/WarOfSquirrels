package fr.AleksGirardey.Handlers;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Objects.Channels.GlobalChannel;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Cubo;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.security.auth.login.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WarHandler {

    private List<War> wars = new ArrayList<>();

    private static ConfigurationNode                                rootNode;
    private static ConfigurationLoader<CommentedConfigurationNode>  manager;

    public      WarHandler(ConfigurationLoader<CommentedConfigurationNode>  configManager) {
        Path    configPath = FileSystems.getDefault().getPath("WarOfSquirrels/config", "WOS.rollbacks");

        try {
            if (!configPath.toFile().exists()) {
                File    conf = configPath.toFile();
                if (!conf.createNewFile())
                    Core.getLogger().error("Can't create WOS.rollbacks");
                ConfigurationLoader<CommentedConfigurationNode>     defaultManager = HoconConfigurationLoader
                        .builder()
                        .setURL(getClass().getClassLoader().getResource("config/WOS.rollbacks"))
                        .build();

                rootNode = defaultManager.load();
                manager = HoconConfigurationLoader.builder().setPath(configPath).build();
                manager.save(rootNode);
            } else
                manager = configManager;
            rootNode = manager.load();

            rootNode.getChildrenList().forEach(this::rollback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void    rollback (ConfigurationNode node) {
        node.getChildrenList().forEach(block -> {
            Optional<World>         optWorld = Core.getPlugin().getServer().getWorld(UUID.fromString(node.getNode("world").getString()));
            World                   world;
            Location<World>         loc;

            if (optWorld.isPresent()) {
                world = optWorld.get();
                loc = world.getLocation(node.getNode("x").getInt(), node.getNode("y").getInt(), node.getNode("z").getInt());

                loc.setBlockType(Core.getPlugin().getRegistry().getType(BlockType.class, node.getNode("type").getString()).get(), Cause.source(Core.getPlugin()).build());
            }
        });
    }

    public boolean  createWar(City attacker, City defender, PartyWar party) {
        if (Core.getCityHandler().areEnemies(attacker, defender)) {
            int     defenders = Core.getCityHandler().getOnlinePlayers(defender).size();

            if (Contains(attacker) || Contains(defender)) {
                party.Send("You can't attack this city ");
                return false;
            }

            if (party.getLeader().getUser().hasPermission("minecraft.command.op") || (defenders > 4 && party.size() < (defenders + 1))) {
                wars.add(new War(attacker, defender, party.toList(), rootNode));
                return true;
            }
            else
                party.getLeader().sendMessage(Text.of("Defenders are not enough"));
        } else
            party.Send("Your city is not enemy with " + defender.getDisplayName());
        return false;
    }

    public War      getWar(DBPlayer player) {
        for (War war : wars)
            if (war.contains(player))
                return war;
        return null;
    }

    public War      getWar(City  city) {
        for (War war : wars)
            if (war.contains(city))
                return war;
        return null;
    }

    public boolean      Contains(DBPlayer player) {
        for (War war : wars)
            if (war.contains(player))
                return true;
        return  false;
    }

    public boolean      Contains(City city) {
        for (War war : wars)
            if (war.contains(city))
                return true;
        return false;
    }

    public boolean      ContainsDefender(City city) {
        for (War war : wars)
            if (war.getDefender() == city)
                return true;
        return false;
    }

    public boolean      ableTo(DBPlayer player, Cubo cubo) {
        City            city = cubo.getOwner().getCity();

        return  getWar(player).getPhase().equals("War")
                && Contains(player)
                && getWar(player).getDefender() == city;
    }

    public boolean      ableTo(DBPlayer player, Chunk chunk) {
        City            city = chunk.getCity();

        return  getWar(player).getPhase().equals("War")
                && Contains(player)
                && getWar(player).getDefender() == city;
    }

    public List<String>     getCitiesList() {
        List<String>        list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getAttacker().getDisplayName());
            list.add(war.getDefender().getDisplayName());
        }
        return list;
    }

    public void     delete(War war) {
        wars.remove(war);
    }

    public void     displayList(DBPlayer player) {
        if (ConfigLoader.peaceTime) {
            player.sendMessage(Text.of("---=== Peace is ON ===---"));
            return;
        }

        player.sendMessage(Text.of("---=== War list [" + wars.size() + "] ===---"));

        for (War war : wars)
            player.sendMessage(Text.of(war.getAttacker().getDisplayName() + " [" + war.getAttackerPoints() + "] vs. "
                    + war.getDefender().getDisplayName() + " [" + war.getDefenderPoints() + "]"));
    }

    public void     AddPoints(DBPlayer killer, DBPlayer victim) {
        War         war = getWar(killer);

        if (war.isDefender(killer) && war.isAttacker(victim))
            war.addDefenderPoints();
        else if (war.isAttacker(killer) && war.isDefender(victim)) {
            if (war.isTarget(victim))
                war.addAttackerPointsTarget();
            else
                war.addAttackerPoints();
        }
    }

    public boolean      isConcerned(Vector3i position) {
        Chunk           c = Core.getChunkHandler().get(position.getX() / 16, position.getZ() / 16);

        return c != null && getWar(c.getCity()).getDefender() == c.getCity();
    }
}
