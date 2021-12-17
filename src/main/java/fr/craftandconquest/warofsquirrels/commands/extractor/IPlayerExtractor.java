package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface IPlayerExtractor {
    String playerNameArgument = "[PlayerName]";

    default String getRawPlayer(CommandContext<CommandSourceStack> context) {
        return context.getArgument(playerNameArgument, String.class);
    }

    default FullPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(getRawPlayer(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getPlayerRegister() {
        return Commands.argument(playerNameArgument, StringArgumentType.string());
    }
}
