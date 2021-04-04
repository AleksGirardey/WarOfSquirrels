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
    /* Claiming */
    @Getter @Setter private int   minPartySizeToCreateCity;
    @Getter @Setter private int   distanceCities;
    @Getter @Setter private int   distanceOutpost;

    /* Influence */
    @Getter @Setter private int     territoryClaimLimit;
    @Getter @Setter private int     baseInfluenceGeneration;
    @Getter @Setter private int     baseInfluenceRequired;

    /* Speaking */
    @Getter @Setter private int   shoutDistance;
    @Getter @Setter private int   sayDistance;

    /* World Config */
    @Getter @Setter private int   mapSize;
    @Getter @Setter private int   mapCenterX;
    @Getter @Setter private int   mapCenterZ;
    @Getter @Setter private int   territorySize;
    @Getter @Setter private boolean   territoriesGenerated;
    @Getter @Setter private boolean   peaceTime;
    @Getter @Setter private int   reincarnationTime;
    @Getter @Setter private int   invitationTime;
    @Getter @Setter private int   startBalance;

    /* War */
    @Getter @Setter private int   preparationPhase;
    @Getter @Setter private int   rollbackPhase;
    @Getter @Setter private int   influenceMax;

    @JsonProperty("CityRanks") @Getter @Setter private Map<Integer, CityRank> cityRankMap;
    @JsonProperty("DefaultPermissions") @Getter @Setter private Map<PermissionRelation, Permission> permissionMap;
    @Getter @Setter private Permission defaultNaturePermission;
}
