package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class WarChannel extends Channel {
    private final War war;

    public WarChannel(War war) {
        super(war.getBroadCastTarget());

        this.war = war;
    }

    @Override
    protected ITextComponent transformText(Player sender, ITextComponent text) {
        return transformTextAnnounce(text);
    }

    @Override
    protected ITextComponent transformTextAnnounce(ITextComponent text) {
        return new StringTextComponent(String.format("[%s] %s", war.getTag(), text));
    }
}
