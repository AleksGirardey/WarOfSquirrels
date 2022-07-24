package fr.craftandconquest.warofsquirrels.object.upgrade;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class UpgradeItem {
    @JsonProperty @Getter @Setter private int amount;
    @JsonProperty @Getter private int itemId = -1;
    @JsonProperty @Getter private String tagId = "NOTSET";

    @JsonIgnore @Getter @Setter private Item item;
    @JsonIgnore @Getter @Setter private TagKey<Item> itemTag;

    @JsonCreator
    public UpgradeItem(@JsonProperty("itemId") int id, @JsonProperty("tagId") String tagId, @JsonProperty("amount") int amount) {
        this.amount = amount;
        setItemId(id);
        setTagId(tagId);
    }

    public UpgradeItem(Item _item) {
        item = _item;
        itemId = Item.getId(item);
    }

    public UpgradeItem(TagKey<Item> _tag) {
        itemTag = _tag;
        tagId = itemTag.location().toString();
    }

    public boolean is(ItemStack stack) {
        return item == null ? stack.is(itemTag) : stack.is(item);
    }

    @JsonSetter public void setItemId(int id) {
        if (id == -1) return;

        itemId = id;
        item = Item.byId(id);
    }

    @JsonSetter public void setTagId(String id) {
        if (id.equals("NOTSET")) return;

        tagId = id;
        itemTag = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(id));
    }

    @JsonIgnore public List<Item> getItems() {
        List<Item> list;
        if (item != null) {
            list = new ArrayList<>();
            list.add(item);
        } else {
            ITagManager<Item> tagManager = ForgeRegistries.ITEMS.tags();

            if (tagManager == null)
                list = new ArrayList<>();
            else
                list = tagManager.getTag(itemTag).stream().toList();
        }
        return list;
    }

    public MutableComponent asString() {
        return MutableComponent.create(item == null ? new TranslatableContents(itemTag.location().toString()) : new TranslatableContents(item.getDescriptionId())); //+ "." + item.getRegistryName()
    }
}
