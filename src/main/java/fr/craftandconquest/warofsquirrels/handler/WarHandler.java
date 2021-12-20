package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.AttackTarget;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.object.world.ChunkLocation;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.permission.PermissionAPI;

import java.util.ArrayList;
import java.util.List;

public class WarHandler {

    private final List<War> wars = new ArrayList<>();

    public WarHandler() {
    }

    private void rollback() {
    }

    public boolean CreateWar(City city, AttackTarget target, Party party) {
        return target instanceof City && CreateWar(city, (City) city, party);
    }

    public boolean CreateWar(City attacker, City defender, Party party) {
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

    public War getWar(FullPlayer player) {
        for (War war : wars) {
            if (war.contains(player))
                return war;
        }
        return null;
    }

    public War getWar(AttackTarget target) {
        return getWar((City) target);
    }

    public War getWar(City city) {
        for (War war : wars) {
            if (war.contains(city))
                return war;
        }
        return null;
    }

    public boolean Contains(FullPlayer player) {
        return getWar(player) != null;
    }

    public boolean Contains(City city) {
        return getWar(city) != null;
    }

    public boolean ContainsDefender(City city) {
        War war = getWar(city);

        return war != null && war.getCityDefender() == city;
    }

    public boolean AbleTo(FullPlayer player, Chunk chunk) {
        return AbleTo(player, chunk.getCity()) && !chunk.getHomeBlock() && !chunk.getOutpost();
    }

    public boolean AbleTo(FullPlayer player, City city) {
        return ContainsDefender(city)
                && Contains(player)
                && getWar(city).getState().equals(War.WarState.War);
    }

    public boolean AbleTo(FullPlayer player, Cubo cubo) {
        return AbleTo(player, cubo.getOwner().getCity());
    }

    public List<String> getCitiesList() {
        List<String> list = new ArrayList<>();

        for (War war : wars) {
            list.add(war.getCityAttacker().displayName);
            list.add(war.getCityDefender().displayName);
        }
        return list;
    }

    public void Delete(War war) {
        wars.remove(war);
    }

    public void DisplayList(FullPlayer player) {
        if (WarOfSquirrels.instance.getConfig().isPeaceTime()) {
            player.getPlayerEntity().sendMessage(ChatText.Success("---=== Time is at peace ===---"), Util.NIL_UUID);
            return;
        }

        if (!wars.isEmpty()) {
            player.getPlayerEntity().sendMessage(ChatText.Success("---=== Battles [" + wars.size() + "] ===---"), Util.NIL_UUID);

            for (War war : wars)
                player.getPlayerEntity().sendMessage(ChatText.Success(war.getCityAttacker().getDisplayName() + " [" + war.getAttackersPoints().getScore() + "] vs. "
                        + war.getCityDefender().getDisplayName() + " [" + war.getDefendersPoints().getScore() + "]"), Util.NIL_UUID);
        } else
            player.getPlayerEntity().sendMessage(ChatText.Success("There is no battles at this moment."), Util.NIL_UUID);
    }

    public void AddPoints(FullPlayer killer, FullPlayer victim) {
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

//    public boolean IsConcerned(Vector2 position, ResourceKey<Level> dimensionId) {
//        return IsConcerned(new ChunkLocation((int) position.x / 16, (int) position.y / 16, dimensionId));
//    }
//
//    public boolean IsConcerned(ChunkLocation chunkLocation) {
//
//    }
    public boolean IsConcerned(Chunk chunk) {
        if (chunk == null) return false;

        War war = getWar(chunk.getCity());

        return war != null && war.getCityDefender() == chunk.getCity();
    }
}
