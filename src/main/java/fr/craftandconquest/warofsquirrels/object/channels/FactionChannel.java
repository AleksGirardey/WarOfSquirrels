package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public class FactionChannel extends Channel {

    private final Faction faction;

    public FactionChannel(Faction faction) {
        super(BroadCastTarget.FACTION);

        this.faction = faction;
    }

    @Override
    public MutableComponent transformText(FullPlayer sender, MutableComponent text) {
        return ChatText.Colored(String.format("[%s][%s] ",
                sender.getCity().getDisplayName(), Utils.getDisplayNameWithRank(sender)), ChatFormatting.DARK_BLUE)
                .append(text);
    }

    @Override
    protected MutableComponent transformTextAnnounce(MutableComponent text) {
        return ChatText.Colored(String.format("[%s] ", faction.getDisplayName()), ChatFormatting.GOLD)
                .append(text);
    }
}
