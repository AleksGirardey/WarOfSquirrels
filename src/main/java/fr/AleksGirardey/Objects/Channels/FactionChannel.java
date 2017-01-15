package fr.AleksGirardey.Objects.Channels;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Faction;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.*;

public class                        FactionChannel implements MutableMessageChannel {

    private Faction                 faction;
    private Set<MessageReceiver>    members;

    public FactionChannel(Faction faction, Collection<MessageReceiver> members) {
        this.faction = faction;
        this.members = Collections.newSetFromMap(new WeakHashMap<>());
        this.members.addAll(members);
        Core.getLogger().info("[Chat] Faction channel created (" + faction.getDisplayName() + ")");
    }

    public FactionChannel(Faction faction) { this(faction, Collections.emptySet()); }

    @Override
    public Optional<Text>       transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        return Optional.of(Text.of(TextColors.DARK_BLUE, original, TextColors.RESET));
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
    public void clearMembers() { this.members.clear(); }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return this.members;
    }

    public Faction      getFaction() { return faction; }
}
