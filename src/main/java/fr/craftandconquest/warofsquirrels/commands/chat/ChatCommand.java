package fr.craftandconquest.warofsquirrels.commands.chat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public abstract class ChatCommand extends CommandBuilder {
    public abstract String commandName();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal(commandName()).executes(this);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.setChatTarget(BroadCastTarget.valueOf(commandName().toUpperCase()));
        player.sendMessage(new TextComponent("Chat toggle to " + commandName().toUpperCase()).withStyle(ChatFormatting.GOLD));

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Cannot perform this command");
    }
}
