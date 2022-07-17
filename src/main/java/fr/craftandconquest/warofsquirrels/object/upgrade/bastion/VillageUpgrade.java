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

public class VillageUpgrade extends Upgrade {
    @JsonIgnore
    public static Map<Integer, VillageUpgrade> defaultVillageUpgrade;

    static {
        VillageUpgrade zero = new VillageUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 64);
            put(new UpgradeItem(ItemTags.PLANKS), 64);
        }});

        VillageUpgrade one = new VillageUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.BREAD), 16);
            put(new UpgradeItem(Items.BEEF), 32);
            put(new UpgradeItem(Items.COBBLESTONE), 96);
            put(new UpgradeItem(ItemTags.PLANKS), 96);
        }});

        VillageUpgrade two = new VillageUpgrade(2, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.ANVIL), 1);
            put(new UpgradeItem(Items.CLOCK), 1);
            put(new UpgradeItem(ItemTags.BEDS), 4);
            put(new UpgradeItem(Items.BOOKSHELF), 8);
            put(new UpgradeItem(Items.COOKED_BEEF), 32);
            put(new UpgradeItem(Items.COAL), 64);
        }});

        VillageUpgrade three = new VillageUpgrade(3, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.CRAFTING_TABLE), 8);
            put(new UpgradeItem(Items.FURNACE), 8);
            put(new UpgradeItem(ItemTags.BEDS), 8);
            put(new UpgradeItem(ItemTags.WOOL), 32);
            put(new UpgradeItem(Items.BRICKS), 64);
            put(new UpgradeItem(Items.GLASS), 64);
            put(new UpgradeItem(Items.COAL), 128);
        }});

        defaultVillageUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public VillageUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public VillageUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Village Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultVillageUpgrade.get(level).upgradeItems;
    }
}
