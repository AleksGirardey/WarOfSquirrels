package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public abstract class FactionMayorOrAssistantCommandBuilder extends FactionCommand {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && (
                player.getCity().getFaction().getCapital().getOwner().equals(player) ||
                player.getCity().getOwner().equals(player));
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You need to be assistant or leader in your faction to perform this command")
                .withStyle(ChatFormatting.BOLD);
    }
}
