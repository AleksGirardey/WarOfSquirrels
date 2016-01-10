package fr.AleksGirardey.Commands.CityCommand.Set;

import fr.AleksGirardey.Commands.CityCommand.CityCommand;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandSetHelp extends CityCommand {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        player.sendMessage(Text.of("--==| City set help |==--"));
        player.sendMessage(Text.of("/city set spawn"));
        player.sendMessage(Text.of("/city set mayor [player]"));
        player.sendMessage(Text.of("/city set assistant [player]"));
        return CommandResult.success();
    }
}
