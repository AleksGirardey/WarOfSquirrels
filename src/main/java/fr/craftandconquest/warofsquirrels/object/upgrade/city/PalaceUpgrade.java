package fr.craftandconquest.warofsquirrels.object.upgrade.city;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import lombok.Getter;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PalaceUpgrade extends Upgrade {
    @JsonIgnore public static Map<Integer, PalaceUpgrade> defaultPalaceUpgrade;

    static {
        PalaceUpgrade zero = new PalaceUpgrade(0, 0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.GOLDEN_HELMET), 1);
            put(new UpgradeItem(Items.GOLD_BLOCK), 5);
        }});

        PalaceUpgrade one = new PalaceUpgrade(1, 0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.GOLD_BLOCK), 18);
            put(new UpgradeItem(Items.LAPIS_BLOCK), 4);
            put(new UpgradeItem(Items.EMERALD_BLOCK), 1);
        }});

        PalaceUpgrade two = new PalaceUpgrade(2, 5, 3, new HashMap<>() {{
            put(new UpgradeItem(Items.GOLD_BLOCK), 64);
            put(new UpgradeItem(Items.LAPIS_BLOCK), 16);
            put(new UpgradeItem(Items.EMERALD_BLOCK), 16);
            put(new UpgradeItem(Items.DIAMOND_BLOCK), 12);
            put(new UpgradeItem(Items.GHAST_TEAR), 8);
        }});

        PalaceUpgrade three = new PalaceUpgrade(3, 14, 8, new HashMap<>() {{
            put(new UpgradeItem(Items.DARK_PRISMARINE), 32);
            put(new UpgradeItem(Items.NETHERITE_INGOT), 64);
            put(new UpgradeItem(Items.EMERALD_BLOCK), 1);
            put(new UpgradeItem(Items.GOLD_BLOCK), 128);
            put(new UpgradeItem(Items.LAPIS_BLOCK), 32);
            put(new UpgradeItem(Items.EMERALD_BLOCK), 32);
            put(new UpgradeItem(Items.DIAMOND_BLOCK), 16);
            put(new UpgradeItem(Items.PURPUR_BLOCK), 32);
            put(new UpgradeItem(Items.DRAGON_BREATH), 1);
            put(new UpgradeItem(Items.DRAGON_HEAD), 1);
            put(new UpgradeItem(Items.NETHER_STAR), 1);
        }});

        defaultPalaceUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonProperty @Getter int minBastion;

    @JsonCreator
    public PalaceUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("minBastion") int bastion, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
        minBastion = bastion;
    }

    public PalaceUpgrade(int index, int delay, int bastion, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        minBastion = bastion;
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Palace Upgrade";
    }

    @Override
    public boolean isComplete(Upgradable target) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultPalaceUpgrade.get(level).upgradeItems;
    }
}
