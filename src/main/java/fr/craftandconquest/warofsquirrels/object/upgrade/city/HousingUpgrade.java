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

public class HousingUpgrade extends Upgrade {
    @JsonIgnore public static Map<Integer, HousingUpgrade> defaultHousingUpgrade;

    static {
        HousingUpgrade zero = new HousingUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 320);
            put(new UpgradeItem(ItemTags.PLANKS), 128);
        }});

        HousingUpgrade one = new HousingUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 128);
            put(new UpgradeItem(ItemTags.LOGS), 128);
            put(new UpgradeItem(ItemTags.WOOL), 128);
            put(new UpgradeItem(Items.GLASS), 64);
        }});

        HousingUpgrade two = new HousingUpgrade(2, 1, new HashMap<>() {{
            put(new UpgradeItem(Items.STONE_BRICKS), 512);
            put(new UpgradeItem(ItemTags.LOGS), 384);
            put(new UpgradeItem(ItemTags.WOOL), 256);
            put(new UpgradeItem(Items.GLASS), 128);
            put(new UpgradeItem(Items.DIAMOND), 48);
        }});

        HousingUpgrade three = new HousingUpgrade(3, 2, new HashMap<>() {{
            put(new UpgradeItem(Items.STONE_BRICKS), 3456);
            put(new UpgradeItem(ItemTags.LOGS), 640);
            put(new UpgradeItem(Items.BRICKS), 512);
            put(new UpgradeItem(Items.DIAMOND), 64);
        }});

        defaultHousingUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public HousingUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public HousingUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Housing Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultHousingUpgrade.get(level).upgradeItems;
    }
}
