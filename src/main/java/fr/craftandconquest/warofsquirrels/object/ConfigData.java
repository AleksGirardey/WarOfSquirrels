package fr.craftandconquest.warofsquirrels.object;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
