package fr.AleksGirardey.Commands;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public abstract class           Commands implements CommandExecutor {
    protected boolean           CanDoIt(DBPlayer player) { return true; }

    protected abstract boolean  SpecialCheck(DBPlayer player, CommandContext context);

    protected abstract CommandResult   ExecCommand(DBPlayer player, CommandContext context);

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

            if (CanDoIt(player) && SpecialCheck(player, commandContext))
                return ExecCommand(player, commandContext);
        }
        return CommandResult.empty();
    }
}
