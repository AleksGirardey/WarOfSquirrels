package fr.craftandconquest.warofsquirrels.object.channels;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class FactionChannel extends Channel {

    private final Faction faction;

    public FactionChannel(Faction faction) {
        super();

        this.faction = faction;
    }

    @Override
    public void SendAnnounce(ITextComponent message) {
        for (Player player : receivers) {
            player.getPlayerEntity().sendMessage(new StringTextComponent(
                    String.format("[%s] %s", faction.getDisplayName(), message))
                    .applyTextStyle(TextFormatting.GOLD));
        }
    }

    @Override
    public ITextComponent transformText(Player sender, ITextComponent text) {
        return new StringTextComponent(String.format("[%s][%s] ",
                sender.getCity().displayName, Utils.getDisplayNameWithRank(sender)))
                .applyTextStyle(TextFormatting.DARK_BLUE);
    }
}
