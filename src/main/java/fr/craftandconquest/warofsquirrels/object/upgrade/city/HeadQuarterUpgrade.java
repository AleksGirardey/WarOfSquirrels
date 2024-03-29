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

public class HeadQuarterUpgrade extends Upgrade {
    @JsonIgnore public static Map<Integer, HeadQuarterUpgrade> defaultHeadQuarterUpgrade;

    static {
        HeadQuarterUpgrade zero = new HeadQuarterUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COMPASS), 1);
            put(new UpgradeItem(Items.DIAMOND_HELMET), 1);
            put(new UpgradeItem(Items.IRON_SWORD), 1);
        }});

        HeadQuarterUpgrade one = new HeadQuarterUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(ItemTags.PLANKS), 192);
            put(new UpgradeItem(Items.COBBLESTONE), 192);
            put(new UpgradeItem(Items.BOOK), 1);
            put(new UpgradeItem(Items.MAP), 1);
            put(new UpgradeItem(ItemTags.BANNERS), 1);
        }});

        HeadQuarterUpgrade two = new HeadQuarterUpgrade(2, 1, new HashMap<>() {{
            put(new UpgradeItem(Items.REDSTONE), 512);
            put(new UpgradeItem(Items.GUNPOWDER), 256);
            put(new UpgradeItem(Items.BONE), 256);
            put(new UpgradeItem(Items.ROTTEN_FLESH), 256);
            put(new UpgradeItem(Items.ENDER_PEARL), 64);
            put(new UpgradeItem(ItemTags.BANNERS), 5);
        }});

        HeadQuarterUpgrade three = new HeadQuarterUpgrade(2, 6, new HashMap<>() {{
            put(new UpgradeItem(Items.REDSTONE), 1024);
            put(new UpgradeItem(Items.GUNPOWDER), 512);
            put(new UpgradeItem(Items.ENDER_PEARL), 256);
            put(new UpgradeItem(Items.FIRE_CHARGE), 256);
            put(new UpgradeItem(Items.BLAZE_ROD), 256);
            put(new UpgradeItem(Items.OBSIDIAN), 192);
            put(new UpgradeItem(Items.CHEST_MINECART), 24);
            put(new UpgradeItem(Items.FLINT_AND_STEEL), 5);
        }});

        defaultHeadQuarterUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public HeadQuarterUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public HeadQuarterUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Head Quarter Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultHeadQuarterUpgrade.get(level).upgradeItems;
    }
}
