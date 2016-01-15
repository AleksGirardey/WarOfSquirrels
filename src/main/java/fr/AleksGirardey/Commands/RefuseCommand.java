package fr.AleksGirardey.Commands;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class RefuseCommand implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            if (Core.getInvitationHandler().refuseInvitation(player))
                return CommandResult.success();
        }

        return CommandResult.empty();
    }
}
