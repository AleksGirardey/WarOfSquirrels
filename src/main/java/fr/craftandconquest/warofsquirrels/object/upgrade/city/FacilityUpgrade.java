package fr.craftandconquest.warofsquirrels.object.upgrade.city;

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

public class FacilityUpgrade extends Upgrade {
    @JsonIgnore public static Map<Integer, FacilityUpgrade> defaultFacilityUpgrade;

    static {
        FacilityUpgrade zero = new FacilityUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.SLABS), 320);
        }});

        FacilityUpgrade one = new FacilityUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.SLABS), 768);
            put(new UpgradeItem(ItemTags.FENCES), 256);
            put(new UpgradeItem(Items.IRON_SHOVEL), 8);
            put(new UpgradeItem(Items.MAP), 1);
        }});

        FacilityUpgrade two = new FacilityUpgrade(2, 2, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.SLABS), 1152);
            put(new UpgradeItem(ItemTags.FENCES), 384);
            put(new UpgradeItem(Items.WHITE_CONCRETE_POWDER), 384);
            put(new UpgradeItem(Items.RAIL), 128);
        }});

        FacilityUpgrade three = new FacilityUpgrade(3, 4, new HashMap<>() {{
            put(new UpgradeItem(Items.REDSTONE), 1024);
            put(new UpgradeItem(Items.WHITE_CONCRETE_POWDER), 896);
            put(new UpgradeItem(Items.RAIL), 768);
            put(new UpgradeItem(Items.COPPER_INGOT), 640);
            put(new UpgradeItem(Items.POWERED_RAIL), 256);
            put(new UpgradeItem(Items.MINECART), 27);
        }});

        defaultFacilityUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public FacilityUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public FacilityUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Facility Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultFacilityUpgrade.get(level).upgradeItems;
    }
}
