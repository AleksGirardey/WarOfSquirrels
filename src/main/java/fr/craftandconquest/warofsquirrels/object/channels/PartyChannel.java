package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PartyChannel extends Channel {

    public PartyChannel() {
        super(BroadCastTarget.PARTY);
    }

    @Override
    protected ITextComponent transformText(Player sender, ITextComponent text) {
        return new StringTextComponent(String.format("[Groupe][%s] ", sender.getDisplayName()))
                .applyTextStyle(TextFormatting.GRAY);
    }

    @Override
    protected ITextComponent transformTextAnnounce(ITextComponent text) {
        return new StringTextComponent(String.format("[Groupe] %s", text))
                .applyTextStyle(TextFormatting.GOLD);
    }
}
