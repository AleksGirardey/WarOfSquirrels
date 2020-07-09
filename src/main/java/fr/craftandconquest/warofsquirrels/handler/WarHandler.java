package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.PartyWar;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

public class WarHandler {

    private List<War> wars = new ArrayList<>();

    public WarHandler() {}

    private void rollback() {}

    public boolean CreateWar(City city, AttackTarget target, PartyWar party) {
        return target instanceof City && CreateWar(city, (City) city, party);
    }

    public boolean CreateWar(City attacker, City defender, PartyWar party) {
        if (WarOfSquirrels.instance.getFactionHandler().areEnemies(attacker.getFaction(), defender.getFaction())) {
            int defenders = defender.getOnlinePlayers().size();

            if (Contains(attacker) || Contains(defender)) {
                party.Send("You cannot attack this city, they already are at war !");
                return false;
            }

            if (PermissionAPI.hasPermission(party.getLeader().getPlayerEntity(), "minecraft.command.op")
                    || defenders > 4 && party.size() < (defenders + 1)) {
                wars.add(new War(attacker, defender, party.toList()));
                return true;
            } else
                party.Send("You cannot attack this city, they are not enough !");
        } else
            party.Send("You cannot attack this city, she is not your enemy !");
        return false;
    }

    public War getWar(Player player) {
        for (War war : wars) {
            if (war.contains(player))
                return war;
        }
        return null;
    }

    public War getWar(City city) {
        for (War war : wars) {
            if (war.contains(city))
                return war;
        }
        return null;
    }

    public boolean Contains(Player player) {
        return getWar(player) != null;
    }

    public boolean Contains(City city) {
        return getWar(city) != null;
    }

    public boolean ContainsDefender(City city) {
        War war = getWar(city);

        return war != null && war.getCityDefender() == city;
    }

    public boolean AbleTo(Player player, Chunk chunk) {
        return AbleTo(player, chunk.getCity()) && !chunk.getHomeBlock() && !chunk.getOutpost();
    }

    public boolean AbleTo(Player player, City city) {
        return ContainsDefender(city)
                && Contains(player)
                && getWar(city).getState().equals(War.WarState.War);
    }

    public boolean AbleTo(Player player, Cubo cubo) {
        return AbleTo(player, cubo.getOwner().getCity());
    }

    public List<String>     getCitiesList() {
        List<String>        list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getCityAttacker().displayName);
            list.add(war.getCityDefender().displayName);
        }
        return list;
    }

    public void Delete(War war) {
        wars.remove(war);
    }

    public void DisplayList(Player player) {
        if (WarOfSquirrels.instance.getConfig().isPeaceTime()) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("---=== Time is at peace ===---"));
            return;
        }

        if (!wars.isEmpty()) {
            player.getPlayerEntity().sendMessage(new StringTextComponent("---=== Battles [" + wars.size() + "] ===---"));

            for (War war : wars)
                player.getPlayerEntity().sendMessage(new StringTextComponent(war.getCityAttacker().getDisplayName() + " [" + war.getAttackersPoints().getScorePoints() + "] vs. "
                        + war.getCityDefender().getDisplayName() + " [" + war.getDefendersPoints().getScorePoints() + "]"));
        } else
            player.getPlayerEntity().sendMessage(new StringTextComponent("There is no battles at this moment."));
    }

    public void AddPoints(Player killer, Player victim) {
        War war = getWar(killer);

        if (!war.getState().equals(War.WarState.War)) return;

        if (war.isDefender(killer) && war.isAttacker(victim))
            war.AddDefenderKillPoints();
        else if (war.isAttacker(killer) && war.isDefender(victim)) {
            if (war.isTarget(victim))
                war.AddAttackerTargetKillPoints();
            else
                war.AddAttackerKillPoints();
        }
    }

    public boolean IsConcerned(Vector2 position, int dimensionId) {
        Chunk chunk = WarOfSquirrels.instance.getChunkHandler().getChunk(
                (int) position.x / 16, (int) position.y / 16, dimensionId);

        if (chunk == null) return false;

        War war = getWar(chunk.getCity());

        return war != null && war.getCityDefender() == chunk.getCity();
    }
}
