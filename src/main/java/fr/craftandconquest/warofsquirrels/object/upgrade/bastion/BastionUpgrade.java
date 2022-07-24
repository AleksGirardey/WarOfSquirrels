package fr.craftandconquest.warofsquirrels.object.upgrade.bastion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeInfo;
import lombok.Getter;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;

public class BastionUpgrade {
    public enum UpgradeType {
        Level,
        Village,
        Barrack,
        Fortification,
        Road,
    }

    @JsonProperty @Getter private UpgradeInfo level;
    @JsonProperty @Getter private UpgradeInfo village;
    @JsonProperty @Getter private UpgradeInfo barrack;
    @JsonProperty @Getter private UpgradeInfo fortification;
    @JsonProperty @Getter private UpgradeInfo road;

    @JsonProperty private LevelUpgrade currentLevelUpgrade;
    @JsonProperty private VillageUpgrade currentVillageUpgrade;
    @JsonProperty private BarrackUpgrade currentBarrackUpgrade;
    @JsonProperty private FortificationUpgrade currentFortificationUpgrade;
    @JsonProperty private RoadUpgrade currentRoadUpgrade;

    @JsonIgnore private Bastion bastion;

    public void Init(Bastion bastion) {
        level = new UpgradeInfo();
        village = new UpgradeInfo();
        barrack = new UpgradeInfo();
        fortification = new UpgradeInfo();
        road = new UpgradeInfo();

        this.bastion = bastion;

        currentLevelUpgrade = LevelUpgrade.defaultLevelUpgrade.get(level.getCurrentLevel());
        currentVillageUpgrade = VillageUpgrade.defaultVillageUpgrade.get(village.getCurrentLevel());
        currentBarrackUpgrade = BarrackUpgrade.defaultBarrackUpgrade.get(barrack.getCurrentLevel());
        currentFortificationUpgrade = FortificationUpgrade.defaultFortificationUpgrade.get(fortification.getCurrentLevel());
        currentRoadUpgrade = RoadUpgrade.defaultRoadUpgrade.get(road.getCurrentLevel());
    }

    public boolean CompleteUpgrade(UpgradeType type) {
        Upgrade target = null;
        UpgradeInfo targetInfo = null;

        switch (type) {
            case Level -> {
                target = currentLevelUpgrade;
                targetInfo = level;
            }
            case Village -> {
                target = currentVillageUpgrade;
                targetInfo = village;
            }
            case Barrack -> {
                target = currentBarrackUpgrade;
                targetInfo = barrack;
            }
            case Fortification -> {
                target = currentFortificationUpgrade;
                targetInfo = this.fortification;
            }
            case Road -> {
                target = currentRoadUpgrade;
                targetInfo = road;
            }
        }

        if (target == null) return false;

        if (target.isComplete(bastion)) {
            targetInfo.setHasBeenComplete(true);
            targetInfo.setDaysBeforeLevelUp(target.getDelayInDays());
            if (target.getDelayInDays() <= 0)
                LevelUp(type);
            return true;
        }

        return false;
    }

    public void LevelUp(UpgradeType type) {
        UpgradeInfo info = null;

        switch (type) {
            case Level -> info = level;
            case Village -> info = village;
            case Barrack -> info = barrack;
            case Fortification -> info = fortification;
            case Road -> info = road;
        }

        if (info == null) {
            return;
        }

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
            case Village -> target = currentVillageUpgrade;
            case Barrack -> target = currentBarrackUpgrade;
            case Fortification -> target = currentFortificationUpgrade;
            case Road -> target = currentRoadUpgrade;
        }

        if (target == null) return 0;
        return target.fill(container);
    }

    public void SetNextTarget(UpgradeType type) {
        switch (type) {
            case Level -> {
                currentLevelUpgrade = LevelUpgrade.defaultLevelUpgrade.getOrDefault(level.getCurrentLevel(), null);
                if (currentLevelUpgrade != null)
                    currentLevelUpgrade.applyCostReduction(bastion.getCostReduction(), level.getCurrentLevel());
            }
            case Village -> {
                currentVillageUpgrade = VillageUpgrade.defaultVillageUpgrade.getOrDefault(village.getCurrentLevel(),  null);
                if (currentVillageUpgrade != null)
                    currentVillageUpgrade.applyCostReduction(bastion.getCostReduction(), village.getCurrentLevel());
            }
            case Barrack -> {
                currentBarrackUpgrade = BarrackUpgrade.defaultBarrackUpgrade.getOrDefault(barrack.getCurrentLevel(), null);
                if (currentBarrackUpgrade != null)
                    currentBarrackUpgrade.applyCostReduction(bastion.getCostReduction(), barrack.getCurrentLevel());
            }
            case Fortification -> {
                currentFortificationUpgrade = FortificationUpgrade.defaultFortificationUpgrade.getOrDefault(fortification.getCurrentLevel(), null);
                if (currentFortificationUpgrade != null)
                    currentFortificationUpgrade.applyCostReduction(bastion.getCostReduction(), fortification.getCurrentLevel());
            }
            case Road -> {
                currentRoadUpgrade = RoadUpgrade.defaultRoadUpgrade.getOrDefault(road.getCurrentLevel(), null);
                if (currentRoadUpgrade != null)
                    currentRoadUpgrade.applyCostReduction(bastion.getCostReduction(), fortification.getCurrentLevel());
            }
        }
    }

    public void VerifyLevelUp() {
        for (UpgradeType type : UpgradeType.values())
            LevelUp(type);
    }

    public void Populate(Bastion bastion) {
        this.bastion = bastion;

        if (currentLevelUpgrade != null) currentLevelUpgrade.Populate();
        if (currentVillageUpgrade != null) currentVillageUpgrade.Populate();
        if (currentFortificationUpgrade != null) currentFortificationUpgrade.Populate();
        if (currentBarrackUpgrade != null) currentBarrackUpgrade.Populate();
        if (currentRoadUpgrade != null) currentRoadUpgrade.Populate();
    }

    @JsonIgnore
    public int getMaxChunk() {
        switch (level.getCurrentLevel()) {
            case 0, 1, 2, 3 -> { return 16; }
            case 4 -> { return 25; }
        }
        return 16;
    }

    @JsonIgnore
    public int getSelfInfluenceGenerated() {
        switch (village.getCurrentLevel()) {
            case 1 -> { return 15; }
            case 2 -> { return 25; }
            case 3 -> { return 35; }
            case 4 -> { return 50; }
        }
        return 0;
    }

    @JsonIgnore
    public int getInfluenceGeneratedDistantNeighbour() { return 0; }

    @JsonIgnore
    public int getInfluenceGeneratedCloseNeighbour(boolean onNeutral) {
        int fromVillage;
        int fromRoad = 0;

        switch (village.getCurrentLevel()) {
            case 1 -> fromVillage = 15;
            case 2 -> fromVillage = 25;
            case 3 -> fromVillage = 35;
            case 4 -> fromVillage = 50;
            default -> fromVillage = 0;
        }

        if (onNeutral) {
            switch (road.getCurrentLevel()) {
                case 1 -> fromRoad = 30;
                case 2 -> fromRoad = 50;
                default -> fromRoad = 0;
            }
        }

        return fromVillage + fromRoad;
    }

    @JsonIgnore public int getInfluenceRange() { return 0; }

    @JsonIgnore public int getInfluenceMax() {
        switch (fortification.getCurrentLevel()) {
            case 1 -> { return 250; }
            case 2 -> { return 500; }
            case 3 -> { return 750; }
            case 4 -> { return 1000; }
        }
        return 0;
    }

    @JsonIgnore
    public UpgradeInfo getUpgradeInfo(UpgradeType type) {
        switch (type) {
            case Level -> { return level; }
            case Village -> { return village; }
            case Barrack -> { return barrack; }
            case Fortification -> { return fortification; }
            case Road -> { return road; }
        }
        return level;
    }

    public boolean CanFill(UpgradeType type) {
        UpgradeInfo info = getUpgradeInfo(type);

        if (info.getCurrentLevel() >= 4) return false;

        if (type == UpgradeType.Level) return true;

        return info.getCurrentLevel() < level.getCurrentLevel();
    }

    public MutableComponent asString(BastionUpgrade.UpgradeType type) {
        switch (type) {
            case Level -> { return currentLevelUpgrade.asString(); }
            case Village -> { return currentVillageUpgrade.asString(); }
            case Barrack -> { return currentBarrackUpgrade.asString(); }
            case Fortification -> { return currentFortificationUpgrade.asString(); }
            case Road -> { return currentRoadUpgrade.asString(); }
        }
        return asString();
    }

    public MutableComponent asString() {
        return currentLevelUpgrade.asString()
                .append(currentVillageUpgrade.asString())
                .append(currentBarrackUpgrade.asString())
                .append(currentFortificationUpgrade.asString())
                .append(currentRoadUpgrade.asString());
    }

    public MutableComponent displayInfo() {
        MutableComponent message = MutableComponent.create(ComponentContents.EMPTY);

        message.append("  Level [" + getLevel().getCurrentLevel() + "/4]\n");
        message.append("  Village [" + getVillage().getCurrentLevel() + "/4]\n");
        message.append("  Barrack [" + getBarrack().getCurrentLevel() + "/4]\n");
        message.append("  Fortification [" + getFortification().getCurrentLevel() + "/4]\n");
        message.append("  Road [" + getRoad().getCurrentLevel() + "/4]\n");

        return message;
    }
}
