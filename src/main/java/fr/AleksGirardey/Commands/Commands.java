package fr.AleksGirardey.Commands;

<<<<<<< HEAD
import fr.AleksGirardey.Objects.Core;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
<<<<<<< HEAD
import org.spongepowered.api.entity.living.player.Player;

public abstract class Commands implements CommandExecutor {
    protected boolean CanDoIt(Player player) {
        return true;
    }

    protected abstract boolean SpecialCheck(Player player, CommandContext context);

    protected abstract CommandResult   ExecCommand(Player player, CommandContext context);

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            if (CanDoIt(player) && SpecialCheck(player, commandContext))
                return ExecCommand(player, commandContext);
        }
        return CommandResult.empty();
=======

public class Commands implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        return null;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }
}
