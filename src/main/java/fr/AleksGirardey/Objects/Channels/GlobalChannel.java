package fr.AleksGirardey.Objects.Channels;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

import javax.annotation.Nullable;
import java.util.*;

public class GlobalChannel implements MutableMessageChannel{

    private Set<MessageReceiver>        members;

    public GlobalChannel() {
        this(Collections.emptySet());
    }

    public GlobalChannel(Collection<MessageReceiver> members) {
        this.members = Collections.newSetFromMap(new WeakHashMap<>());
        this.members.addAll(members);
        Core.getLogger().info("[Chat] Global channel created");
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

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.unmodifiableSet(this.members);
    }

    @Override
    public Optional<Text>       transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {
        DBPlayer                receiver = Core.getPlayerHandler().get((Player) recipient);
        return Optional.of(Text.of(Utils.getChatTag(receiver), original.toString().substring(original.toString().indexOf(" "))));
    }

    @Override
    public void send(Text original) {

    }

    @Override
    public void send(Text original, ChatType type) {
    }

    @Override
    public void send(@Nullable Object sender, Text original, ChatType type) {
    }

    public void         send(Object sender, Text original) {
        if (sender == null)
            Core.Send("Sender is NULL");
        Text    text = transformMessage(sender, (MessageReceiver) sender, original, ChatTypes.CHAT).get();
        Player  player = (Player) sender;

        for (MessageReceiver r : members) {
            Player p = (Player) r;
            if (player.getLocation().getPosition().distance(p.getLocation().getPosition()) >= 30.0)
                p.sendMessage(text);
        }
    }
}
