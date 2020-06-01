package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class WorldChannel extends Channel {
    public WorldChannel() {
        super(BroadCastTarget.GENERAL);
    }

    @Override
    protected ITextComponent transformText(Player sender, ITextComponent text) {
        return new StringTextComponent(String.format("[ANNOUNCE] %s", sender.getDisplayName()))
                .applyTextStyle(TextFormatting.RED);
    }

    @Override
    protected ITextComponent transformTextAnnounce(ITextComponent text) {
        return new StringTextComponent("");
    }
}
