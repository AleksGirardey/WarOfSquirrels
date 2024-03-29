package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.Upgradable;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public abstract class Upgrade {
    @JsonIgnore @Getter @Setter protected Map<UpgradeItem, Integer> upgradeItems;

    @JsonProperty @Getter @Setter protected List<UpgradeItem> upgradeItemList;
    @JsonProperty @Getter protected int upgradeIndex;
    @JsonProperty @Getter protected int delayInDays;

    protected Upgrade(int index, int delay, List<UpgradeItem> list) {
        upgradeIndex = index;
        delayInDays = delay;
        upgradeItemList = new ArrayList<>(list);
    }

    protected Upgrade(int index, int delay) {
        upgradeIndex = index;
        delayInDays = delay;
    }

    protected void fillList() {
        upgradeItemList = new ArrayList<>();
        upgradeItems.forEach((key, value) -> {
            key.setAmount(value);
            upgradeItemList.add(key);
        });
    }

    @JsonIgnore protected abstract String getUpgradeName();
    @JsonIgnore public abstract boolean isComplete(Upgradable target);
    @JsonIgnore protected abstract Map<UpgradeItem, Integer> getDefaultValues(int level);

    @JsonIgnore protected String getExtraRequirements() {
        if (delayInDays <= 0) return "";

        return "Requires :\n  -" + delayInDays + " days to complete the upgrade.\n";
    }

    public boolean contains(Container container) {
        Set<Item> list = new HashSet<>();

        upgradeItems.keySet().forEach(u -> list.addAll(u.getItems()));

        return container.hasAnyOf(list);
    }

    public void Populate() {
        upgradeItems = new HashMap<>();

        upgradeItemList.forEach(item -> upgradeItems.put(item, item.getAmount()));
    }

    public int fill(Container container) {
        int amount = 0;

        if (!contains(container)) return amount;

        for (int index = 0; index < container.getContainerSize(); ++index) {
            ItemStack stack = container.getItem(index);

            for(Map.Entry<UpgradeItem, Integer> entry : upgradeItems.entrySet()) {
                if (entry.getKey().is(stack)) {
                    int amountToRemove = stack.getCount();

                    amountToRemove = Math.min(amountToRemove, entry.getValue());

                    amount += amountToRemove;

                    container.removeItem(index, amountToRemove);
                    entry.setValue(entry.getValue() - amountToRemove);
                }
            }
        }
        fillList();
        return amount;
    }

    protected boolean hasCompleteItems() {
        return upgradeItems.values().stream().noneMatch(amount -> amount > 0);
    }

    public void applyCostReduction(int percentage, int level) {
        Map<UpgradeItem, Integer> defaultValues = getDefaultValues(level);

        upgradeItems.entrySet().forEach((entry) -> {
            int oldValue = entry.getValue();
            int defaultValue = defaultValues.get(entry.getKey());
            int newValue = Math.max(0, oldValue + ((defaultValue * percentage) / 100));

//            WarOfSquirrels.instance.debugLog("[ApplyCostReduction] " + oldValue
//                    + " - " + defaultValue
//                    + " - " + percentage
//                    + " - " + ((defaultValue * percentage) / 100)
//                    + " - " + newValue);

            entry.getKey().setAmount(newValue);
            entry.setValue(newValue);
        });
    }

    public MutableComponent asString() {
        MutableComponent title = MutableComponent.create(ComponentContents.EMPTY);
        MutableComponent entryComponent = MutableComponent.create(ComponentContents.EMPTY);
        MutableComponent extra = MutableComponent.create(ComponentContents.EMPTY);
        int count = 0;

        title.withStyle(ChatFormatting.DARK_GREEN)
                .append("== ").append(getUpgradeName()).append(" [" + upgradeIndex + "/" + "4] ==\n");
        for(Map.Entry<UpgradeItem, Integer> entry : upgradeItems.entrySet()) {
            if (entry.getValue() <= 0) continue;
            ++count;
            entryComponent.append("- ").withStyle(ChatFormatting.GREEN)
                    .append(Utils.SplitToStack(entry.getValue()).withStyle(ChatFormatting.BLUE))
                    .append(" (" + entry.getValue() + ") ").withStyle(ChatFormatting.GREEN)
                    .append(entry.getKey().asString().withStyle(ChatFormatting.BLUE))
                    .append("\n");
        }

        if (count == 0)
            entryComponent = ChatText.Success("Upgrade is ready to be completed ! Refer to /.. [UpgradeType] complete command\n").withStyle(ChatFormatting.GREEN);

        extra.append(getExtraRequirements());

        return title.append(entryComponent).append(extra);
    }
}
