package fr.AleksGirardey.Commands.CityCommand.Set;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandSetHelp implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            player.sendMessage(Text.of("--==| City set help |==--"));
            player.sendMessage(Text.of("/city set spawn"));
            //player.sendMessage("/city set mayor [player]");
            //player.sendMessage("/city set assistant [player]");
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
