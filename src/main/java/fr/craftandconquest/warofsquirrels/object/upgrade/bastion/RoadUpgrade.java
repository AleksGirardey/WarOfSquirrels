package fr.craftandconquest.warofsquirrels.object.upgrade.bastion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadUpgrade extends Upgrade {
    @JsonIgnore
    public static Map<Integer, RoadUpgrade> defaultRoadUpgrade;

    static {
        RoadUpgrade zero = new RoadUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.SLABS), 64);
        }});

        RoadUpgrade one = new RoadUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.SLABS), 128);
        }});

        RoadUpgrade two = new RoadUpgrade(2, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.ENCHANTING_TABLE), 1);
            put(new UpgradeItem(Items.GOLDEN_HELMET), 1);
            put(new UpgradeItem(Items.CHEST_MINECART), 2);
            put(new UpgradeItem(Items.RAIL), 64);
            put(new UpgradeItem(Items.BREAD), 64);
        }});

        RoadUpgrade three = new RoadUpgrade(3, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.ENCHANTING_TABLE), 2);
            put(new UpgradeItem(Items.GOLDEN_BOOTS), 4);
            put(new UpgradeItem(Items.CHEST_MINECART), 6);
            put(new UpgradeItem(Items.POWERED_RAIL), 16);
            put(new UpgradeItem(Items.RAIL), 128);
            put(new UpgradeItem(Items.SUGAR), 256);
        }});

        defaultRoadUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public RoadUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public RoadUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Road Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultRoadUpgrade.get(level).upgradeItems;
    }
}
