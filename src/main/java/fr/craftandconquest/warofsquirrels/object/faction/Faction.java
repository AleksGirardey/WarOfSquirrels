package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.*;
import fr.craftandconquest.warofsquirrels.object.scoring.IScoreUpdater;
import fr.craftandconquest.warofsquirrels.object.scoring.Score;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class Faction implements IPermission, IChannelTarget, IScoreUpdater {

    @JsonProperty @Getter private UUID factionUuid;
    @JsonProperty @Getter @Setter private UUID capitalUuid;
    @JsonProperty @Getter @Setter private String displayName;
    @JsonIgnore @Getter private City capital;
    @JsonIgnore @Getter private Map<String, City> cities = new HashMap<>();
    @JsonIgnore @Getter @Setter private Map<IPermission, Permission> customPermission = new HashMap<>();
    @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission;
    @Getter @Setter private List<CustomPermission> customPermissionList = new ArrayList<>();

    @JsonProperty @Setter private Score score = new Score();

    public Faction(String displayName, City capital) {
        this.factionUuid = UUID.randomUUID();
        this.displayName = displayName;
        this.capital = capital;
        this.capitalUuid = capital.getCityUuid();
    }

    public void SetCapital(City city) {
        this.capital = city;
        this.capitalUuid = city.getCityUuid();
    }

    public void addCity(City city) {
        if (!cities.containsKey(city.getDisplayName()))
            cities.put(city.getDisplayName(), city);
    }

    public void updateDependencies() {
        this.capital = WarOfSquirrels.instance.getCityHandler().getCity(this.capitalUuid);

        for (CustomPermission permission : customPermissionList) {
            IPermission target = permission.getTarget();

            if (target == null) continue;

            customPermission.put(target, permission.permission);
        }
    }

    public void displayInfo(FullPlayer player) {
        MutableComponent message = ChatText.Colored("", ChatFormatting.DARK_GREEN);

        message.append("--==| " + displayName + " [" + cities.size() + "] |==--\n");
        message.append(" Owner: " + this.capital.getOwner().getDisplayName() + "\n");
        message.append(" Capital: " + this.capital.getDisplayName() + "\n");
        message.append(" Cities: ");

        String[] citiesNames = cities.keySet().toArray(new String[0]);

        for (int i = 0; i < citiesNames.length; ++i) {
            message.append(citiesNames[i]);
            if (i != citiesNames.length - 1)
                message.append(", ");
        }

        message.append("\n Territories: ");

        List<Territory> territories = WarOfSquirrels.instance.getTerritoryHandler().getAll().stream()
                .filter(territory -> territory.getFaction() != null)
                .filter(t -> t.getFaction().equals(this)).toList();

        for (int i = 0; i < territories.size(); ++i) {
            message.append(territories.get(i).getName());
            if (i != territories.size() - 1)
                message.append(", ");
        }

        message.append("\n Allies: ");

        List<Faction> allies = WarOfSquirrels.instance.getDiplomacyHandler().getAllies(this);

        for (int i = 0; i < allies.size(); ++i) {
            message.append(allies.get(i).getDisplayName());
            if (i != allies.size() - 1)
                message.append(", ");
        }
        message.append("\n Enemies: ");

        List<Faction> enemies = WarOfSquirrels.instance.getDiplomacyHandler().getEnemies(this);

        for (int i = 0; i < enemies.size(); ++i) {
            message.append(enemies.get(i).getDisplayName());
            if (i != enemies.size() - 1)
                message.append(", ");
        }

        message.append("\n Permissions:\n").append(displayPermissions());

        player.sendMessage(message);
    }

    public String displayPermissions() {
        StringBuilder permissionsAsString = new StringBuilder();

        permissionsAsString.append("== Default Permission [Build|Container|Switch|Farm|Interact] ==\n");

        defaultPermission.forEach((k, v) ->
                permissionsAsString.append("  ").append(k.toString()).append(" ").append(v.toString()).append("\n"));

        if (customPermissionList.size() > 0) {
            permissionsAsString.append("== Custom Permission [Build|Container|Switch|Farm|Interact] ==\n");

            customPermission.forEach((k, v) ->
                    permissionsAsString.append("  ").append(k.getPermissionDisplayName()).append(" ").append(v.toString()).append("\n"));
        }

        return permissionsAsString.toString();
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.FACTION;
    }

    @Override
    public UUID getUuid() {
        return factionUuid;
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.FACTION;
    }

    @Override
    public String getPermissionDisplayName() {
        return "F:" + getDisplayName();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", displayName, capital.displayName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        Faction faction = (Faction) obj;

        return faction.getUuid().equals(this.factionUuid);
    }

    public void update() {
        updateScore();
    }

    @Override
    public Score getScore() { return score; }

    @Override
    public void updateScore() {
        List<FullPlayer> players = new ArrayList<>();
        int todayWarScore = 0;
        int todayScore = 0;

        for (City city : cities.values()) {
            todayScore = city.getScore().getTodayScore();
            todayWarScore = city.getScore().getTodayWarScore();
            city.getScore().UpdateScore();
            players.addAll(city.getCitizens());
        }

        int ratioPlayers = Math.min(players.size() / 20, 1);
        int scoreToPlayers = Math.round(score.getTodayScore() * 0.2f) / ratioPlayers;

        for (FullPlayer player : players) {
            player.getScore().AddScore(scoreToPlayers);
        }

        score.AddScore(todayScore + todayWarScore);
        score.UpdateScore();
    }
}