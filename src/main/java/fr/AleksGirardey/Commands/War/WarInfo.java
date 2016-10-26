package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.War.War;
=======
import fr.AleksGirardey.Objects.War;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
<<<<<<< HEAD
=======
import org.spongepowered.api.event.block.ChangeBlockEvent;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

public class WarInfo implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (!(commandSource instanceof Player))
            return CommandResult.empty();
        Player  player = (Player) commandSource;
        if (commandContext.hasAny("[city]")) {
            commandContext.<War>getOne("[city]").get().displayInfo(player);
            return CommandResult.success();
        }
        if (Core.getWarHandler().getWar(player) == null)
            return CommandResult.empty();
        Core.getWarHandler().getWar(player).displayInfo(player);
        return CommandResult.success();
    }
}
