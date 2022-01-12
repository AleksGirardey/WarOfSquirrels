package fr.craftandconquest.warofsquirrels.object.upgrade.bastion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.upgrade.Upgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FortificationUpgrade extends Upgrade {
    @JsonIgnore
    public static Map<Integer, FortificationUpgrade> defaultFortificationUpgrade;

    static {
        FortificationUpgrade zero = new FortificationUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 192);
            put(new UpgradeItem(Items.IRON_DOOR), 2);
        }});

        FortificationUpgrade one = new FortificationUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.IRON_DOOR), 4);
            put(new UpgradeItem(Items.WATER_BUCKET), 6);
            put(new UpgradeItem(Items.OBSIDIAN), 8);
            put(new UpgradeItem(Items.COBBLESTONE), 256);
        }});

        FortificationUpgrade two = new FortificationUpgrade(2, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.OBSIDIAN), 32);
            put(new UpgradeItem(Items.BLACKSTONE), 32);
            put(new UpgradeItem(Items.STONE_BRICKS), 192);
        }});

        FortificationUpgrade three = new FortificationUpgrade(3, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.IRON_DOOR), 12);
            put(new UpgradeItem(Items.DIAMOND), 32);
            put(new UpgradeItem(Items.BLACKSTONE), 64);
            put(new UpgradeItem(Items.OBSIDIAN), 96);
            put(new UpgradeItem(Items.STONE_BRICKS), 256);
            put(new UpgradeItem(Items.REDSTONE), 256);
        }});

        defaultFortificationUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public FortificationUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public FortificationUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Fortification Upgrade";
    }

    @Override
    public boolean isComplete(IFortification fortification) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultFortificationUpgrade.get(level).upgradeItems;
    }
}
