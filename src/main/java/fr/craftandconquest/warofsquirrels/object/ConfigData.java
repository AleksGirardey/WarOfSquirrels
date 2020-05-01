package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.city.CityRank;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class ConfigData {
    @Getter @Setter private int   distanceCities;
    @Getter @Setter private int   distanceOutpost;
    @Getter @Setter private int   shoutDistance;
    @Getter @Setter private int   sayDistance;
    @Getter @Setter private int   peaceTime;
    @Getter @Setter private int   reincarnationTime;
    @Getter @Setter private int   startBalance;
    @Getter @Setter private int   preparationPhase;
    @Getter @Setter private int   rollbackPhase;
    @Getter @Setter private int   mapSize;
    @Getter @Setter private int   territoriesGenerated;
    @Getter @Setter private int   territorySize;
    @Getter @Setter private int   territoryClaimLimit;
    @JsonProperty("CityRanks") @Getter @Setter private Map<Integer, CityRank> cityRankMap;
    @JsonProperty("DefaultPermissions") @Getter @Setter private Map<PermissionRelation, Permission> permissionMap;
}
