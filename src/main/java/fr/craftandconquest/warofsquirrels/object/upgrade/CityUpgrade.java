package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.upgrade.city.LevelUpgrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.world.Container;

@AllArgsConstructor
@NoArgsConstructor
public class CityUpgrade {
    public enum UpgradeType {
        Level,
        Housing,
        Facility,
        HeadQuarter,
        Palace,
    }

    @JsonProperty @Getter private int level = 0;
    @JsonProperty @Getter private int housing = -1;
    @JsonProperty @Getter private int facility = -1;
    @JsonProperty @Getter private int headQuarter = -1;
    @JsonProperty @Getter private int palace = -1;

    @JsonProperty private LevelUpgrade currentLevelUpgrade;

    public void Init() {
        currentLevelUpgrade = LevelUpgrade.levelUpgradeMap.get(1);
    }

    public boolean CompleteUpgrade(UpgradeType type) {
//        switch (type) {
//            case Level -> {
//                if (currentLevelUpgrade.isComplete())
//            }
//            case Housing -> {}
//            case Facility -> {}
//            case HeadQuarter -> {}
//            case Palace -> {}
//        }
        return true;
    }

    public void FillUpgrade(UpgradeType type, Container container) {
        switch (type) {
            case Level -> currentLevelUpgrade.fill(container);
            case Housing -> {}
            case Facility -> {}
            case HeadQuarter -> {}
            case Palace -> {}
        }
    }

    @Override
    public String toString() {
        return currentLevelUpgrade.toString();
    }
}
