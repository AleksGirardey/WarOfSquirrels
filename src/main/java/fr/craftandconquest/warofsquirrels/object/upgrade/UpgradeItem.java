package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UpgradeItem {
    @JsonProperty @Getter @Setter private int amount;
    @JsonProperty @Getter private int itemId = -1;
    @JsonProperty @Getter private String tagId = "NOTSET";

    @JsonIgnore @Getter @Setter private Item item;
    @JsonIgnore @Getter @Setter private Tag.Named<Item> itemTag;

    public UpgradeItem(Item _item) {
        item = _item;
        itemId = Item.getId(item);
    }

    public UpgradeItem(Tag.Named<Item> _tag) {
        itemTag = _tag;
        tagId = itemTag.getName().toString();
    }

    public boolean is(ItemStack stack) {
        return item == null ? stack.is(itemTag) : stack.is(item);
    }

    @JsonSetter public void setItemId(int id) {
        if (id == -1) return;

        item = Item.byId(id);
    }

    @JsonSetter public void setTagId(String id) {
        if (id.equals("NOTSET")) return;

        itemTag = ItemTags.bind(id);
    }

    @JsonIgnore public List<Item> getItems() {
        List<Item> list;
        if (item != null) {
            list = new ArrayList<>();
            list.add(item);
        } else {
            list = itemTag.getValues();
        }
//        WarOfSquirrels.LOGGER.info("[WoS][Debug] Looking for items : " + list);
        return list;
    }

    public String toString() {
        return item == null ? itemTag.getName().toString() : item.toString();//Objects.requireNonNull(item.getRegistryName()).toString();
    }
}
