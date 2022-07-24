package fr.craftandconquest.warofsquirrels.commands.chat;

import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;

public class ChatFaction extends ChatCommand {
    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (player.getCity() != null && player.getCity().getFaction() != null) return true;

        player.sendMessage(ChatText.Error("You do not belong to a faction."), true);
        return false;
    }

    @Override
    public String commandName() {
        return "faction";
    }
}
