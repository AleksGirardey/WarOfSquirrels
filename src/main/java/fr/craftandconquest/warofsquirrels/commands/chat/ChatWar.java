package fr.craftandconquest.warofsquirrels.commands.chat;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;

public class ChatWar extends ChatCommand {
    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.isInWar()) return true;

        player.sendMessage(ChatText.Error("You do not belong to a war."), true);
        return false;
    }

    @Override
    public String commandName() {
        return "war";
    }
}
