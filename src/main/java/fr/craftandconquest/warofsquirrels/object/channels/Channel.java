package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.Getter;
import net.minecraft.util.text.ITextComponent;
import java.util.ArrayList;
import java.util.List;

public abstract class Channel {

    @Getter protected final List<Player> receivers;
    @Getter private final BroadCastTarget broadCastType;

    public Channel(BroadCastTarget targetType) {
        broadCastType = targetType;
        receivers = new ArrayList<>();
    }

    public boolean addMember(Player player) {
        return receivers.add(player);
    }

    public boolean removeMember(Player player) {
        return receivers.remove(player);
    }

    public boolean clearMembers() {
        receivers.clear();
        return true;
    }

    protected abstract ITextComponent transformText(Player sender, ITextComponent text);
    protected abstract ITextComponent transformTextAnnounce(ITextComponent text);

    public void SendAnnounce(ITextComponent message) {
        for (Player player : receivers) {
            player.getPlayerEntity().sendMessage(transformTextAnnounce(message));
        }
    }

    public void SendMessage(Player sender, ITextComponent message) {
        for(Player player : receivers) {
            player.getPlayerEntity().sendMessage(transformText(sender, message));
        }
    }
}
