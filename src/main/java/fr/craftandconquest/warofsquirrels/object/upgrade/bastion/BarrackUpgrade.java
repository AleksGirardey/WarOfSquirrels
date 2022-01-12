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

public class BarrackUpgrade extends Upgrade {
    @JsonIgnore
    public static Map<Integer, BarrackUpgrade> defaultBarrackUpgrade;

    static {
        BarrackUpgrade zero = new BarrackUpgrade(0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.IRON_SWORD), 8);
            put(new UpgradeItem(Items.BOW), 8);
            put(new UpgradeItem(Items.ARROW), 64);
        }});

        BarrackUpgrade one = new BarrackUpgrade(1, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.DIAMOND_SWORD), 2);
            put(new UpgradeItem(Items.IRON_SWORD), 8);
            put(new UpgradeItem(Items.BOW), 8);
            put(new UpgradeItem(Items.ARROW), 64);
            put(new UpgradeItem(Items.LAVA_BUCKET), 1);
        }});

        BarrackUpgrade two = new BarrackUpgrade(2, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.DIAMOND_SWORD), 8);
            put(new UpgradeItem(Items.BOW), 8);
            put(new UpgradeItem(Items.ARROW), 64);
            put(new UpgradeItem(Items.ENDER_PEARL), 4);
            put(new UpgradeItem(Items.DIAMOND_PICKAXE), 2);
        }});

        BarrackUpgrade three = new BarrackUpgrade(3, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.DIAMOND_SWORD), 8);
            put(new UpgradeItem(Items.BOW), 8);
            put(new UpgradeItem(Items.ARROW), 64);
            put(new UpgradeItem(Items.ENDER_PEARL), 8);
            put(new UpgradeItem(Items.DIAMOND_PICKAXE), 3);
            put(new UpgradeItem(Items.LADDER), 128);
        }});

        defaultBarrackUpgrade = new HashMap<>() {{
            put(0, zero);
            put(1, one);
            put(2, two);
            put(3, three);
        }};
    }

    @JsonCreator
    public BarrackUpgrade(@JsonProperty("upgradeIndex") int index, @JsonProperty("delayInDays") int delay, @JsonProperty("upgradeItemList") List<UpgradeItem> list) {
        super(index, delay, list);
    }

    public BarrackUpgrade(int index, int delay, Map<UpgradeItem, Integer> map) {
        super(index, delay);
        upgradeItems = new HashMap<>(map);
        upgradeItemList = new ArrayList<>();

        fillList();
    }

    @Override
    protected String getUpgradeName() {
        return "Barrack Upgrade";
    }

    @Override
    public boolean isComplete(IFortification fortification) {
        return hasCompleteItems();
    }

    @Override
    protected Map<UpgradeItem, Integer> getDefaultValues(int level) {
        return defaultBarrackUpgrade.get(level).upgradeItems;
    }
}
