package fr.AleksGirardey.Commands;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class AcceptCommand implements CommandExecutor{

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);
            if (Core.getInvitationHandler().handleInvitation(player, true))
                return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
