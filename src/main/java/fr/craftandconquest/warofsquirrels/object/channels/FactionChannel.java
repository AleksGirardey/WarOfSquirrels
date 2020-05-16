package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionChannel extends Channel {

    private final Faction faction;

    public FactionChannel(Faction faction) {
        super(BroadCastTarget.FACTION);

        this.faction = faction;
    }

    @Override
    public ITextComponent transformText(Player sender, ITextComponent text) {
        return new StringTextComponent(String.format("[%s][%s] ",
                sender.getCity().displayName, Utils.getDisplayNameWithRank(sender)))
                .applyTextStyle(TextFormatting.DARK_BLUE);
    }

    @Override
    protected ITextComponent transformTextAnnounce(ITextComponent text) {
        return new StringTextComponent(
                String.format("[%s] %s", faction.getDisplayName(), text))
                .applyTextStyle(TextFormatting.GOLD);
    }
}
