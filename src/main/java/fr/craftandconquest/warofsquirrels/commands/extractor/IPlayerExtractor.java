package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import lombok.SneakyThrows;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.player.Player;

public interface IPlayerExtractor {
    String playerNameArgument = "[PlayerName]";

    @SneakyThrows
    default Player getRawPlayer(CommandContext<CommandSourceStack> context) {
        return EntityArgument.getPlayer(context, playerNameArgument);
    }

    default FullPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(getRawPlayer(context).getUUID());
    }

    default RequiredArgumentBuilder<CommandSourceStack, EntitySelector> getPlayerRegister() {
        return Commands.argument(playerNameArgument, EntityArgument.player());
    }
}
