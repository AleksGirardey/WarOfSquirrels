package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandLeave extends CityCommand {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return !Core.getPlayerHandler().isOwner(player);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        if (Core.getPlayerHandler().<Boolean>getElement(player, "player_assistant"))
            Core.getPlayerHandler().setElement(player, "player_assistant", false);
        Core.getPlayerHandler().setElement(player, "player_cityId", null);
        return CommandResult.success();
    }
}
