package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public abstract class CityCommand implements CommandExecutor {

    protected boolean CanDoIt(Player player) {
        return Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null;
    };

    protected abstract boolean SpecialCheck(Player player, CommandContext context);

    protected abstract CommandResult   ExecCommand(Player player, CommandContext context);

    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            if (CanDoIt(player) && SpecialCheck(player, commandContext))
                return ExecCommand(player, commandContext);
        }
        return CommandResult.empty();
    }
}
