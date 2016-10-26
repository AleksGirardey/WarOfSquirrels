package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.War.War;
=======
import fr.AleksGirardey.Objects.War;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class WarLeave extends CityCommand {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return Core.getWarHandler().Contains(player);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        War war = Core.getWarHandler().getWar(player);

        if (war.removePlayer(player))
            return CommandResult.success();
        return CommandResult.empty();
    }
}
