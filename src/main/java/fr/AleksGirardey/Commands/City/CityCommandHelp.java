package fr.AleksGirardey.Commands.City;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandHelp implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            player.sendMessage(Text.of("--==| City help |==--"));
            player.sendMessage(Text.of("/city create <name>"));
            player.sendMessage(Text.of("/city info [name]"));
            player.sendMessage(Text.of("/city claim"));
            player.sendMessage(Text.of("/city unclaim"));
            player.sendMessage(Text.of("/city set ..."));
        }
        return CommandResult.empty();
    }
}
