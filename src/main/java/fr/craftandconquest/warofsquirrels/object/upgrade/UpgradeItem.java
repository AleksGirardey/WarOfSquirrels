package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import lombok.AllArgsConstructor;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
public class UpgradeItem {
    @JsonProperty private Item item;
    @JsonProperty private Tag.Named<Item> itemTag;

    public UpgradeItem(Item _item) { item = _item; }
    public UpgradeItem(Tag.Named<Item> _tag) { itemTag = _tag; }

    public boolean is(ItemStack stack) {
        return item == null ? stack.is(itemTag) : stack.is(item);
    }

    public List<Item> getItems() {
        List<Item> list;
        if (item != null) {
            list = new ArrayList<>();
            list.add(item);
        } else {
            list = itemTag.getValues();
        }
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Looking for items : " + list);
        return list;
    }

    public String toString() {
        return item == null ? itemTag.getName().toString() : Objects.requireNonNull(item.getRegistryName()).toString();
    }
}
