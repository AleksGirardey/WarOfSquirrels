package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class UpgradeInfo {
    @JsonProperty @Getter @Setter private int currentLevel = 0;
    @JsonProperty @Getter @Setter private boolean hasBeenComplete = false;
    @JsonProperty @Getter @Setter private int daysBeforeLevelUp = 0;

    public UpgradeInfo(int level) { currentLevel = level; }

    public void LevelUp() {
        ++currentLevel;
        hasBeenComplete = false;
        daysBeforeLevelUp = 0;
    }
}
