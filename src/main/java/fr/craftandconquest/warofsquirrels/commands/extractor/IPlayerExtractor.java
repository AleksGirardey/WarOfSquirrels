package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface IPlayerExtractor {
    String playerNameArgument = "[PlayerName]";

    default String getRawPlayer(CommandContext<CommandSource> context) {
        return context.getArgument(playerNameArgument, String.class);
    }

    default Player getPlayer(CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(getRawPlayer(context));
    }

    default RequiredArgumentBuilder<CommandSource, String> getPlayerRegister() {
        return Commands.argument(playerNameArgument, StringArgumentType.string());
    }
}
