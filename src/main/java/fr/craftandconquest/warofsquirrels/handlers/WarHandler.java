package fr.craftandconquest.warofsquirrels.handlers;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import fr.craftandconquest.warofsquirrels.objects.war.War;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WarHandler {

    private List<War> wars = new ArrayList<>();

    private ConfigurationNode                                rootNode;
    private ConfigurationLoader<CommentedConfigurationNode>  manager;

    public      WarHandler(ConfigurationLoader<CommentedConfigurationNode>  configManager) {
        manager = configManager;
        try {
            rootNode = manager.load();
            if (rootNode.hasListChildren())
                rootNode.getChildrenList().forEach(this::rollback);
            rootNode = manager.createEmptyNode();
            manager.save(rootNode);
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

                loc.setBlockType(Core.getPlugin().getRegistry().getType(BlockType.class, node.getNode("type").getString()).get());
            }
        });
    }

    public boolean  createWar(City city, Attackable target, PartyWar party) {
        return target instanceof City && createWar(city, (City) target, party);
    }

    public boolean  createWar(City attacker, City defender, PartyWar party) {
        if (Core.getFactionHandler().areEnemies(attacker.getFaction(), defender.getFaction())) {
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

        return  getWar(player).getPhase().equals("war")
                && Contains(player)
                && getWar(player).getDefender() == city;
    }

    public boolean      ableTo(DBPlayer player, Chunk chunk) {
        City            city = chunk.getCity();

        return  getWar(player).getPhase().equals("war")
                && Contains(player)
                && getWar(player).getDefender() == city
                && !chunk.isHomeblock() && !chunk.isOutpost();
    }

    public List<String>     getCitiesList() {
        List<String>        list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getAttacker().getDisplayName());
            list.add(war.getDefender().getDisplayName());
        }
        return list;
    }

    public void     delete(War war, ConfigurationNode node) {
        rootNode.removeChild(node);
        wars.remove(war);
    }

    public void     displayList(DBPlayer player) {
        if (Core.getConfig().isPeaceTime()) {
            player.sendMessage(Text.of("---=== Nous sommes en temps de paix ===---"));
            return;
        }

        if (!wars.isEmpty()) {
            player.sendMessage(Text.of("---=== Guerres en cours [" + wars.size() + "] ===---"));

            for (War war : wars)
                player.sendMessage(Text.of(war.getAttacker().getDisplayName() + " [" + war.getAttackerPoints() + "] vs. "
                        + war.getDefender().getDisplayName() + " [" + war.getDefenderPoints() + "]"));
        } else
            player.sendMessage(Text.of("Aucune war n'est actuellement en cours."));
    }

    public void     AddPoints(DBPlayer killer, DBPlayer victim) {
        War         war = getWar(killer);

        if (!war.getPhase().equals(War.WarState.War.toString())) return;

        if (war.isDefender(killer) && war.isAttacker(victim))
            war.addDefenderKillPoints();
        else if (war.isAttacker(killer) && war.isDefender(victim)) {
            if (war.isTarget(victim))
                war.addAttackerPointsTarget();
            else
                war.addAttackerKillPoints();
        }
    }

    public boolean      isConcerned(Vector3i position, World world) {
        Chunk           c = Core.getChunkHandler().get(position.getX() / 16, position.getZ() / 16, world);

        return c != null && getWar(c.getCity()) != null && getWar(c.getCity()).getDefender() == c.getCity();
    }

    public ConfigurationLoader<CommentedConfigurationNode>      getManager() { return manager; }
}
