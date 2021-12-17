package fr.craftandconquest.warofsquirrels.commands.chat;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;

public class ChatGeneral extends ChatCommand {
    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    public String commandName() {
        return "general";
    }
}
