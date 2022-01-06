package fr.craftandconquest.warofsquirrels.object.upgrade.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
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
            put(new UpgradeItem(Items.BOOK), 1);
            put(new UpgradeItem(Items.MAP), 1);
            put(new UpgradeItem(ItemTags.BANNERS), 1);
            put(new UpgradeItem(ItemTags.PLANKS), 192);
            put(new UpgradeItem(Items.COBBLESTONE), 192);
        }});

        HeadQuarterUpgrade two = new HeadQuarterUpgrade(2, 1, new HashMap<>() {{
            put(new UpgradeItem(Items.GUNPOWDER), 128);
            put(new UpgradeItem(Items.BONE), 128);
            put(new UpgradeItem(Items.ROTTEN_FLESH), 128);
            put(new UpgradeItem(Items.ENDER_PEARL), 16);
            put(new UpgradeItem(Items.REDSTONE), 256);
            put(new UpgradeItem(ItemTags.BANNERS), 5);
        }});

        HeadQuarterUpgrade three = new HeadQuarterUpgrade(2, 8, new HashMap<>() {{
            put(new UpgradeItem(Items.GUNPOWDER), 512);
            put(new UpgradeItem(Items.ENDER_PEARL), 256);
            put(new UpgradeItem(Items.REDSTONE), 1024);
            put(new UpgradeItem(Items.OBSIDIAN), 72);
            put(new UpgradeItem(Items.FLINT_AND_STEEL), 5);
        }});

        defaultHeadQuarterUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    public HeadQuarterUpgrade(int index, int delay, List<UpgradeItem> list) {
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
    public boolean isComplete(IFortification fortification) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultHeadQuarterUpgrade.get(level).upgradeItems;
    }
}
