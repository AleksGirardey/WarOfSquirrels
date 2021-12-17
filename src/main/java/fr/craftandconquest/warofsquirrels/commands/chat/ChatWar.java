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
        if (WarOfSquirrels.instance.getWarHandler().Contains(player)) return true;

        player.getPlayerEntity().sendMessage(ChatText.Error("You do not belong to a war."), Util.NIL_UUID);
        return false;
    }

    @Override
    public String commandName() {
        return "war";
    }
}
