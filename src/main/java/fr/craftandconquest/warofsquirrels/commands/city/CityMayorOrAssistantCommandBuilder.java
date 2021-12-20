package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public abstract class CityMayorOrAssistantCommandBuilder extends CityCommandBuilder {
    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You need to be at assistant or mayor in your city to perform this command")
                .withStyle(ChatFormatting.BOLD);
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && (player.getAssistant() || player.getCity().getOwner().equals(player));
    }
}
