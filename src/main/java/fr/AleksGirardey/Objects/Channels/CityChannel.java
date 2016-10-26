package fr.AleksGirardey.Objects.Channels;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.*;

public class                        CityChannel implements MutableMessageChannel{
    private int                     cityId;
    private Set<MessageReceiver>    members;

    public CityChannel(int cityId, Collection<MessageReceiver> members) {
        this.cityId = cityId;
        this.members = Collections.newSetFromMap(new WeakHashMap<>());
        this.members.addAll(members);
        Core.getLogger().info("[Chat] City channel created (" + cityId + ")");
    }

    public CityChannel(int cityId) {
        this(cityId, Collections.emptySet());
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        return Optional.of(Text.of(TextColors.BLUE, original, TextColors.RESET));
    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return this.members;
    }

    @Override
    public boolean addMember(MessageReceiver messageReceiver) {
        return this.members.add(messageReceiver);
    }

    @Override
    public boolean removeMember(MessageReceiver messageReceiver) {
        return this.members.remove(messageReceiver);
    }

    @Override
    public void clearMembers() {
        this.members.clear();
    }
}
