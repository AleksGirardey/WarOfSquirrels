package fr.craftandconquest.warofsquirrels.object.upgrade.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.upgrade.CityUpgrade;
import fr.craftandconquest.warofsquirrels.object.upgrade.UpgradeItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.*;

@AllArgsConstructor
public class LevelUpgrade {

    @JsonIgnore public static Map<Integer, LevelUpgrade> levelUpgradeMap;

    static {
        LevelUpgrade upgradeLevelZero = new LevelUpgrade(0,0, 0, new HashMap<>());
        LevelUpgrade upgradeLevelOne = new LevelUpgrade(1, 0, 0, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 384);
            put(new UpgradeItem(ItemTags.PLANKS), 384);
            put(new UpgradeItem(Items.IRON_INGOT), 64);
        }});
        LevelUpgrade upgradeLevelTwo = new LevelUpgrade(2, 7, 2, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 768);
            put(new UpgradeItem(ItemTags.PLANKS), 384);
            put(new UpgradeItem(Items.IRON_INGOT), 256);
        }});
        LevelUpgrade upgradeLevelThree = new LevelUpgrade(3, 14, 6, new HashMap<>() {{
            put(new UpgradeItem(Items.COBBLESTONE), 1728);
            put(new UpgradeItem(ItemTags.PLANKS), 768);
            put(new UpgradeItem(Items.IRON_INGOT), 512);
            put(new UpgradeItem(Items.DIAMOND), 64);
        }});

        levelUpgradeMap = new HashMap<>() {{
            put(0, upgradeLevelZero);
            put(1, upgradeLevelOne);
            put(2, upgradeLevelTwo);
            put(3, upgradeLevelThree);
        }};
    }

    @JsonProperty @Getter int levelIndex;
    @JsonProperty @Getter int minPeople;
    @JsonProperty @Getter int delayInDays;
    @JsonProperty @Getter Map<UpgradeItem, Integer> upgradeItems;

    public boolean contains(Container container) {
        Set<Item> list = new HashSet<>();

        upgradeItems.keySet().forEach(u -> list.addAll(u.getItems()));

        return container.hasAnyOf(list);
    }

    public void fill(Container container) {
        if (!contains(container)) return;

        for (int index = 0; index < container.getContainerSize(); ++index) {
            ItemStack stack = container.getItem(index);

            for(Map.Entry<UpgradeItem, Integer> entry : upgradeItems.entrySet()) {
                if (entry.getKey().is(stack)) {
                    int amountToRemove = stack.getCount();

                    amountToRemove = Math.min(amountToRemove, entry.getValue());

                    container.removeItem(index, amountToRemove);
                    entry.setValue(entry.getValue() - amountToRemove);
                }
            }
        }
    }

    @JsonIgnore @Override
    public LevelUpgrade clone() throws CloneNotSupportedException {
        super.clone();
        return new LevelUpgrade(this.levelIndex, this.minPeople, this.delayInDays, new HashMap<>(upgradeItems));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("== Level Upgrade [").append(levelIndex).append("] ==\n");
        for(Map.Entry<UpgradeItem, Integer> entry : upgradeItems.entrySet()) {
            if (entry.getValue() <= 0) continue;
            builder.append("- ").append(entry.getValue()).append(" ").append(entry.getKey()).append("\n");
        }

        builder.append("Requires a minimum of ")
                .append(minPeople)
                .append(" people in the city and the upgrade will take ")
                .append(delayInDays)
                .append(" days to complete.\n");

        return builder.toString();
    }
}
