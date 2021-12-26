package fr.craftandconquest.warofsquirrels.commands.chat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class ChatHelp extends CommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("help");
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.sendMessage(ChatText.Colored("""
                /chat general : Toggle general chat
                /chat city : Toggle city chat
                /chat faction : Toggle faction chat
                /chat war : Toggle war chat
                /chat party : Toggle party chat
                """, ChatFormatting.GOLD));
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
