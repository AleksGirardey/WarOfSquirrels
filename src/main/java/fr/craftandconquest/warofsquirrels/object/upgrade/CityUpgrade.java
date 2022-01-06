package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.upgrade.city.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.MutableComponent;
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

    @JsonProperty @Getter private UpgradeInfo level;
    @JsonProperty @Getter private UpgradeInfo housing;
    @JsonProperty @Getter private UpgradeInfo facility;
    @JsonProperty @Getter private UpgradeInfo headQuarter;
    @JsonProperty @Getter private UpgradeInfo palace;

    @JsonProperty private LevelUpgrade currentLevelUpgrade;
    @JsonProperty private HousingUpgrade currentHousingUpgrade;
    @JsonProperty private FacilityUpgrade currentFacilityUpgrade;
    @JsonProperty private HeadQuarterUpgrade currentHeadQuarterUpgrade;
    @JsonProperty private PalaceUpgrade currentPalaceUpgrade;

    @JsonIgnore private City city;

    public void Init(City city) {
        level = new UpgradeInfo(1);
        housing = new UpgradeInfo();
        facility = new UpgradeInfo();
        headQuarter = new UpgradeInfo();
        palace = new UpgradeInfo();

        this.city = city;

        currentLevelUpgrade = LevelUpgrade.defaultLevelUpgrade.get(level.getCurrentLevel());
        currentHousingUpgrade = HousingUpgrade.defaultHousingUpgrade.get(housing.getCurrentLevel());
        currentFacilityUpgrade = FacilityUpgrade.defaultFacilityUpgrade.get(facility.getCurrentLevel());
        currentHeadQuarterUpgrade = HeadQuarterUpgrade.defaultHeadQuarterUpgrade.get(headQuarter.getCurrentLevel());
        currentPalaceUpgrade = PalaceUpgrade.defaultPalaceUpgrade.get(palace.getCurrentLevel());
    }

    public boolean CompleteUpgrade(UpgradeType type, IFortification fortification) {
        Upgrade target = null;
        UpgradeInfo targetInfo = null;

        switch (type) {
            case Level -> {
                target = currentLevelUpgrade;
                targetInfo = level;
            }
            case Housing -> {
                target = currentHousingUpgrade;
                targetInfo = housing;
            }
            case Facility -> {
                target = currentFacilityUpgrade;
                targetInfo = facility;
            }
            case HeadQuarter -> {
                target = currentHeadQuarterUpgrade;
                targetInfo = headQuarter;
            }
            case Palace -> {
                target = currentPalaceUpgrade;
                targetInfo = palace;
            }
        }

        if (target == null) return false;

        if (target.isComplete(fortification)) {
            if (target.getDelayInDays() <= 0) LevelUp(type);
            else {
                targetInfo.setHasBeenComplete(true);
                targetInfo.setDaysBeforeLevelUp(target.getDelayInDays());
            }
            return true;
        }

        return false;
    }

    public void LevelUp(UpgradeType type) {
        UpgradeInfo info = null;

        switch (type) {
            case Level -> info = level;
            case Housing -> info = housing;
            case Facility -> info = facility;
            case HeadQuarter -> info = headQuarter;
            case Palace -> info = palace;
        }

        if (info == null) return;

        if (info.isHasBeenComplete()) {
            info.setDaysBeforeLevelUp(Math.min(0, info.getDaysBeforeLevelUp() - 1));
            if (info.getDaysBeforeLevelUp() <= 0) {
                info.LevelUp();
                SetNextTarget(type);
            }
        }
    }

    public int FillUpgrade(UpgradeType type, Container container) {
        Upgrade target = null;

        switch (type) {
            case Level -> target = currentLevelUpgrade;
            case Housing -> target = currentHousingUpgrade;
            case Facility -> target = currentFacilityUpgrade;
            case HeadQuarter -> target = currentHeadQuarterUpgrade;
            case Palace -> target = currentPalaceUpgrade;
        }

        if (target == null) return 0;
         return target.fill(container);
    }

    public void SetNextTarget(UpgradeType type) {
        switch (type) {
            case Level -> {
                currentLevelUpgrade = LevelUpgrade.defaultLevelUpgrade.getOrDefault(level.getCurrentLevel(), null);
                if (currentLevelUpgrade != null)
                    currentLevelUpgrade.applyCostReduction(city.getCostReduction(), level.getCurrentLevel());
            }
            case Housing -> {
                currentHousingUpgrade = HousingUpgrade.defaultHousingUpgrade.getOrDefault(housing.getCurrentLevel(), null);
                if (currentHousingUpgrade != null) {
                    currentHousingUpgrade.applyCostReduction(city.getCostReduction(), housing.getCurrentLevel());
                    if (currentLevelUpgrade != null) currentLevelUpgrade.applyCostReduction(city.getCostReduction(), level.getCurrentLevel());
                    if (currentFacilityUpgrade != null) currentFacilityUpgrade.applyCostReduction(city.getCostReduction(), facility.getCurrentLevel());
                    if (currentHeadQuarterUpgrade != null) currentHeadQuarterUpgrade.applyCostReduction(city.getCostReduction(), headQuarter.getCurrentLevel());
                    if (currentPalaceUpgrade != null) currentPalaceUpgrade.applyCostReduction(city.getCostReduction(), palace.getCurrentLevel());
                }
            }
            case Facility -> {
                currentFacilityUpgrade = FacilityUpgrade.defaultFacilityUpgrade.getOrDefault(facility.getCurrentLevel(), null);
                if (currentFacilityUpgrade != null) currentFacilityUpgrade.applyCostReduction(city.getCostReduction(), facility.getCurrentLevel());
            }
            case HeadQuarter -> {
                currentHeadQuarterUpgrade = HeadQuarterUpgrade.defaultHeadQuarterUpgrade.getOrDefault(headQuarter.getCurrentLevel(), null);
                if (currentHeadQuarterUpgrade != null) currentHeadQuarterUpgrade.applyCostReduction(city.getCostReduction(), headQuarter.getCurrentLevel());
            }
            case Palace -> {
                currentPalaceUpgrade = PalaceUpgrade.defaultPalaceUpgrade.getOrDefault(palace.getCurrentLevel(), null);
                if (currentPalaceUpgrade != null) currentPalaceUpgrade.applyCostReduction(city.getCostReduction(), palace.getCurrentLevel());
            }
        }
    }

    public void VerifyLevelUp() {
        for (UpgradeType type : UpgradeType.values()) {
            LevelUp(type);
        }
    }

    public void Populate(City city) {
        this.city = city;

        if (currentLevelUpgrade != null) currentLevelUpgrade.Populate();
    }

    @JsonIgnore public int getCostReduction() {
        int housingLevel = housing.getCurrentLevel();
        if (housingLevel <= 1) return 0;
        if (housingLevel == 2) return -5;
        if (housingLevel == 3) return -8;
        if (housingLevel == 4) return -10;

        return 0;
    }

    @JsonIgnore public int getInfluenceGeneratedDistantNeighbour() {
        switch (facility.getCurrentLevel()) {
            case 0 -> { return 0; }
            case 1 -> { return 20; }
            case 2 -> { return 50; }
            case 3 -> { return 100; }
            case 4 -> { return 120; }
        }

        return 0;
    }

    @JsonIgnore public int getInfluenceRange() {
        switch (facility.getCurrentLevel()) {
            case 0 -> { return 0; }
            case 1 -> { return 1; }
            case 2, 3 -> { return 2; }
            case 4 -> { return 3; }
        }

        return 0;
    }

    public boolean CanFill(UpgradeType type) {
        UpgradeInfo info = null;

        switch (type) {
            case Level -> info = level;
            case Housing -> info = housing;
            case Facility -> info = facility;
            case HeadQuarter -> info = headQuarter;
            case Palace -> info = palace;
        }

        if (info.getCurrentLevel() >= 4) return false;

        if (type == UpgradeType.Level) return true;

        return info.getCurrentLevel() < level.getCurrentLevel();
    }

    public UpgradeInfo getUpgradeInfo(UpgradeType type) {
        switch (type) {
            case Level -> { return level; }
            case Housing -> { return housing; }
            case Facility -> { return facility; }
            case HeadQuarter -> { return headQuarter; }
            case Palace -> { return palace; }
        }
        return level;
    }

    public MutableComponent asString(UpgradeType type) {
        switch (type) {
            case Level -> { return currentLevelUpgrade.asString(); }
            case Housing -> { return currentHousingUpgrade.asString(); }
            case Facility -> { return currentFacilityUpgrade.asString(); }
            case HeadQuarter -> { return currentHeadQuarterUpgrade.asString(); }
            case Palace -> { return currentPalaceUpgrade.asString(); }
        }

        return asString();
    }

    public MutableComponent asString() {
        return currentLevelUpgrade.asString()
                .append(currentHousingUpgrade.asString())
                .append(currentFacilityUpgrade.asString())
                .append(currentHeadQuarterUpgrade.asString())
                .append(currentPalaceUpgrade.asString());
    }
}
