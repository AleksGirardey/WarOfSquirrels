package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.Getter;
import net.minecraft.util.text.ITextComponent;
import java.util.ArrayList;
import java.util.List;

public abstract class Channel {

    @Getter protected final List<Player> receivers;

    public Channel() {
        receivers = new ArrayList<>();
    }

    public void addMember(Player player) {
        receivers.add(player);
    }

    public void removeMember(Player player) {
        receivers.remove(player);
    }

    public void clearMembers() {
        receivers.clear();
    }

    protected abstract ITextComponent transformText(Player sender, ITextComponent text);

    protected abstract void SendAnnounce(ITextComponent message);

    public void SendMessage(Player sender, ITextComponent message) {
        for(Player player : receivers) {
            player.getPlayerEntity().sendMessage(transformText(sender, message));
        }
    }
}
