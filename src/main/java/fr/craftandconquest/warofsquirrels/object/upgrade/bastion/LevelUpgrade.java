package fr.craftandconquest.warofsquirrels.object.upgrade.bastion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelUpgrade extends Upgrade {
    @JsonIgnore public static Map<Integer, LevelUpgrade> defaultLevelUpgrade;

    static {
        LevelUpgrade zero = new LevelUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.BANNERS), 1);
            put(new UpgradeItem(Items.DIAMOND), 8);
            put(new UpgradeItem(Items.IRON_INGOT), 32);
        }});
        LevelUpgrade one = new LevelUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.IRON_INGOT), 48);
            put(new UpgradeItem(Items.COBBLESTONE), 192);
            put(new UpgradeItem(ItemTags.PLANKS), 192);
        }});
        LevelUpgrade two = new LevelUpgrade(2, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.BANNERS), 3);
            put(new UpgradeItem(Items.ENDER_PEARL), 4);
            put(new UpgradeItem(Items.IRON_INGOT), 64);
            put(new UpgradeItem(ItemTags.LOGS), 64);
            put(new UpgradeItem(Items.COBBLESTONE), 256);
        }});
        LevelUpgrade three = new LevelUpgrade(3, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.GOLDEN_APPLE), 2);
            put(new UpgradeItem(ItemTags.BANNERS), 6);
            put(new UpgradeItem(Items.NETHERITE_INGOT), 8);
            put(new UpgradeItem(Items.ENDER_PEARL), 16);
            put(new UpgradeItem(Items.DIAMOND), 32);
            put(new UpgradeItem(ItemTags.LOGS), 64);
            put(new UpgradeItem(Items.COBBLESTONE), 256);
        }});

        defaultLevelUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public LevelUpgrade(
            @JsonProperty("upgradeIndex") int index,
            @JsonProperty("delayInDays") int delay,
            @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public LevelUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Level Upgrade";
    }

    @Override
    public boolean isComplete(IFortification fortification) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultLevelUpgrade.get(level).upgradeItems;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new LevelUpgrade(this.upgradeIndex, this.delayInDays, new HashMap<>(upgradeItems));
    }
}
