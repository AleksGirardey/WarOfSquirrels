package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.War;
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
