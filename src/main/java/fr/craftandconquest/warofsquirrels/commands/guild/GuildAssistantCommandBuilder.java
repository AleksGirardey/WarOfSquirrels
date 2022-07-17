package fr.craftandconquest.warofsquirrels.commands.guild;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

public abstract class GuildAssistantCommandBuilder extends GuildCommandBuilder {
    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You need to be at assistant in your guild to perform this command")
                .withStyle(ChatFormatting.BOLD);
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && player.getAssistant();
    }
}
