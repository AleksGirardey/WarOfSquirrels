package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;

public class ForceWinDefender implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        War     war = commandContext.<War>getOne("[city]").get();

        war.forceWinDefender();
        return CommandResult.success();
    }
}
