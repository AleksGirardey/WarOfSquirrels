package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

import java.util.Collection;
import java.util.List;

public class WarDisplay {
    private final CustomBossEvents events;
    private final Scoreboard scoreboard;
    private final CustomBossEvent attackerPointsBar;
    private final CustomBossEvent defenderPointsBar;
    private final PlayerTeam attackerTeam;
    private final PlayerTeam defenderTeam;

    private final War targetWar;
    private final String warKey;

    public WarDisplay(War war) {
        targetWar = war;
        events = WarOfSquirrels.server.getCustomBossEvents();
        scoreboard = WarOfSquirrels.server.getScoreboard();

        warKey = (war.getCityAttacker().getDisplayName() + "_" + war.getCityDefender().getDisplayName()).toLowerCase();

        City attacker = war.getCityAttacker();
        City defender = war.getCityDefender();

        attackerPointsBar = CreateWarBoss(new TextComponent(attacker.getDisplayName() + "[0]"), BossEvent.BossBarColor.BLUE);
        defenderPointsBar = CreateWarBoss(new TextComponent(defender.getDisplayName() + "[0]"), BossEvent.BossBarColor.RED);

        attackerTeam = CreatePlayerTeam(warKey + "_attacker", attacker.getDisplayName(), attacker.tag, ChatFormatting.BLUE);
        defenderTeam = CreatePlayerTeam(warKey + "_defender", defender.getDisplayName(), defender.tag, ChatFormatting.RED);

        UpdateScore();
        UpdatePlayers();
    }

    private PlayerTeam CreatePlayerTeam(String teamName, String displayName, String prefix, ChatFormatting color) {
        PlayerTeam team = scoreboard.addPlayerTeam(teamName);

        team.setColor(color);
        team.setPlayerPrefix(new TextComponent("[" + prefix + "] "));
        team.setCollisionRule(Team.CollisionRule.PUSH_OTHER_TEAMS);
        team.setAllowFriendlyFire(false);
        team.setDisplayName(new TextComponent(displayName));

        return team;
    }

    private CustomBossEvent CreateWarBoss(Component text, BossEvent.BossBarColor color) {
        ResourceLocation location = new ResourceLocation(WarOfSquirrels.modRegistryKey, "war_" + warKey);
        CustomBossEvent bossEvent = events.create(location, text);

        bossEvent.setColor(color);
        bossEvent.setValue(0);
        bossEvent.setMax(1000);

        return bossEvent;
    }

    public void UpdateScore() {
        int score = targetWar.getAttackersPoints();
        attackerPointsBar.setValue(score);
        attackerPointsBar.setName(new TextComponent(targetWar.getCityAttacker().getDisplayName() + " [" + score + "]"));

        score = targetWar.getDefendersPoints();
        defenderPointsBar.setValue(score);
        defenderPointsBar.setName(new TextComponent(targetWar.getCityDefender().getDisplayName() + " [" + score + "]"));
    }

    public void AddAttacker(FullPlayer player) {
        AddPlayer(player.getPlayerEntity(), attackerTeam);
    }

    public void RemoveAttacker(FullPlayer player) {
        RemovePlayer(player.getPlayerEntity(), attackerTeam);
    }

    public void AddDefender(FullPlayer player) {
        AddPlayer(player.getPlayerEntity(), defenderTeam);
    }

    public void RemoveDefender(FullPlayer player) {
        RemovePlayer(player.getPlayerEntity(), defenderTeam);
    }

    private void RemovePlayer(Player player, PlayerTeam team) {
        attackerPointsBar.removePlayer((ServerPlayer) player);
        defenderPointsBar.removePlayer((ServerPlayer) player);

        scoreboard.removePlayerFromTeam(player.getScoreboardName(), team);
    }

    private void AddPlayer(Player player, PlayerTeam team) {
        attackerPointsBar.addPlayer((ServerPlayer) player);
        defenderPointsBar.addPlayer((ServerPlayer) player);

        scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
    }

    public void UpdatePlayers() {
        List<ServerPlayer> attendees = targetWar.getAttendees();

        attackerPointsBar.setPlayers(attendees);
        defenderPointsBar.setPlayers(attendees);

        Collection<String> attackers = attackerTeam.getPlayers();
        Collection<String> defenders = defenderTeam.getPlayers();
        List<String> attackersToAdd = targetWar.getAttackers().stream().filter(player -> !attackers.contains(player.getPlayerEntity().getScoreboardName())).map(this::toScoreboardName).toList();
        List<String> defendersToAdd = targetWar.getAttackers().stream().filter(player -> !defenders.contains(player.getPlayerEntity().getScoreboardName())).map(this::toScoreboardName).toList();

        attackersToAdd.forEach(value -> scoreboard.addPlayerToTeam(value, attackerTeam));
        defendersToAdd.forEach(value -> scoreboard.addPlayerToTeam(value, defenderTeam));
    }

    public void Clear() {
        attackerPointsBar.removeAllPlayers();
        defenderPointsBar.removeAllPlayers();

        events.remove(attackerPointsBar);
        events.remove(defenderPointsBar);

        scoreboard.removePlayerTeam(attackerTeam);
        scoreboard.removePlayerTeam(defenderTeam);
    }

    private String toScoreboardName(FullPlayer player) { return player.getPlayerEntity().getScoreboardName(); }
}
