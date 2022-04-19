package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class PartyChannel extends Channel {

    public PartyChannel() {
        super(BroadCastTarget.PARTY);
    }

    @Override
    protected MutableComponent transformText(FullPlayer sender, MutableComponent text) {
        return ChatText.Colored(String.format("[Party][%s] ", sender.getDisplayName()), ChatFormatting.GRAY);
    }

    @Override
    protected MutableComponent transformTextAnnounce(MutableComponent text) {
        return ChatText.Colored("[Party] ", ChatFormatting.GOLD).append(text);
    }
}
