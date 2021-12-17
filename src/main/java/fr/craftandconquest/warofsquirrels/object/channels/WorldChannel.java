package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class WorldChannel extends Channel {
    public WorldChannel() {
        super(BroadCastTarget.GENERAL);
    }

    @Override
    protected MutableComponent transformText(FullPlayer sender, MutableComponent text) {
        return ChatText.Colored("[" + sender.getDisplayName() + "] ", ChatFormatting.WHITE)
//                .append(sender.getDisplayName())
                .append(text);
    }

    @Override
    protected MutableComponent transformTextAnnounce(MutableComponent text) {
        return ChatText.Colored("[ANNOUNCE] ", ChatFormatting.GOLD).append(text);
    }
}
