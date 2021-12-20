package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class WarChannel extends Channel {
    private final War war;

    public WarChannel(War war) {
        super(war.getBroadCastTarget());

        this.war = war;
    }

    @Override
    protected MutableComponent transformText(FullPlayer sender, MutableComponent text) {
        return transformTextAnnounce(text);
    }

    @Override
    protected MutableComponent transformTextAnnounce(MutableComponent text) {
        return ChatText.Colored(String.format("[%s] %s", war.getTag(), text), ChatFormatting.DARK_GREEN);
    }
}
