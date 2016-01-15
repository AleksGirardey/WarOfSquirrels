package fr.AleksGirardey.Commands;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class AcceptCommand implements CommandExecutor{

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            if (Core.getInvitationHandler().acceptInvitation(player))
                return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
