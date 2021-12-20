package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import lombok.Getter;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public abstract class Channel {

    @Getter
    protected final List<FullPlayer> receivers;
    @Getter
    private final BroadCastTarget broadCastType;

    public Channel(BroadCastTarget targetType) {
        broadCastType = targetType;
        receivers = new ArrayList<>();
    }

    public boolean addMember(FullPlayer player) {
        if (receivers.contains(player)) return false;
        WarOfSquirrels.LOGGER.info("[WoS][BroadcastHandler][Channel : " + getBroadCastType().toString() + "] " + player.getDisplayName() + " added.");
        return receivers.add(player);
    }

    public boolean removeMember(FullPlayer player) {
        return receivers.remove(player);
    }

    public boolean clearMembers() {
        receivers.clear();
        return true;
    }

    protected abstract MutableComponent transformText(FullPlayer sender, MutableComponent text);

    protected abstract MutableComponent transformTextAnnounce(MutableComponent text);

    public void SendAnnounce(MutableComponent message) {
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Announce done to " + receivers.size());
        for (FullPlayer player : receivers) {
            player.getPlayerEntity().sendMessage(transformTextAnnounce(message), Util.NIL_UUID);
        }
    }

    public void SendMessage(FullPlayer sender, MutableComponent message) {
        for (FullPlayer player : receivers) {
            player.getPlayerEntity().sendMessage(transformText(sender, message), Util.NIL_UUID);
        }
    }
}
