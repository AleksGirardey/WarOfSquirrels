package fr.craftandconquest.warofsquirrels.object.upgrade.city;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import lombok.Getter;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LevelUpgrade extends Upgrade {

    @JsonIgnore public static Map<Integer, LevelUpgrade> defaultLevelUpgrade;

    static {
        LevelUpgrade upgradeLevelZero = new LevelUpgrade(0,0, 0, new HashMap<>());
        LevelUpgrade upgradeLevelOne = new LevelUpgrade(1, 0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 768);
            put(new UpgradeItem(ItemTags.PLANKS), 640);
            put(new UpgradeItem(Items.IRON_INGOT), 64);
        }});
        LevelUpgrade upgradeLevelTwo = new LevelUpgrade(2, 3, 2, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 1024);
            put(new UpgradeItem(ItemTags.PLANKS), 1024);
            put(new UpgradeItem(Items.DEEPSLATE_BRICKS), 1024);
            put(new UpgradeItem(Items.COPPER_INGOT), 448);
            put(new UpgradeItem(Items.IRON_INGOT), 256);
        }});
        LevelUpgrade upgradeLevelThree = new LevelUpgrade(3, 7, 6, new HashMap<>() {{
            put(new UpgradeItem(Items.POLISHED_DEEPSLATE), 1920);
            put(new UpgradeItem(Items.COBBLESTONE), 1728);
            put(new UpgradeItem(ItemTags.PLANKS), 768);
            put(new UpgradeItem(Items.IRON_INGOT), 512);
            put(new UpgradeItem(Items.COPPER_INGOT), 448);
            put(new UpgradeItem(Items.GOLD_INGOT), 384);
            put(new UpgradeItem(Items.BRICKS), 384);
            put(new UpgradeItem(Items.QUARTZ_BLOCK), 320);
            put(new UpgradeItem(Items.DIAMOND), 64);
        }});

        defaultLevelUpgrade = new HashMap<>() {{
            put(0, upgradeLevelZero);
            put(1, upgradeLevelOne);
            put(2, upgradeLevelTwo);
            put(3, upgradeLevelThree);
        }};
    }

    @JsonProperty @Getter int minPeople;

    @JsonCreator
    public LevelUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("minPeople") int ppl, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
        minPeople = ppl;
    }

    public LevelUpgrade(int index, int ppl, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        minPeople = ppl;
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @JsonIgnore @Override
    public LevelUpgrade clone() throws CloneNotSupportedException {
        super.clone();
        return new LevelUpgrade(this.upgradeIndex, this.minPeople, this.delayInDays, new HashMap<>(upgradeItems));
    }

    @Override
    protected String getUpgradeName() { return "Level Upgrade"; }

    @Override
    protected String getExtraRequirements() {
        if (minPeople <= 0 && delayInDays <= 0) return "";

        StringBuilder builder = new StringBuilder();
        builder.append("Requires :\n");

        if (minPeople > 0)
            builder.append("  -").append(minPeople).append(" people in the city.\n");
        if (delayInDays > 0)
            builder.append("  -").append(delayInDays).append(" days to complete the upgrade.\n");

        return builder.toString();
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return target.Size() >= minPeople && hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultLevelUpgrade.get(level).upgradeItems;
    }
}
