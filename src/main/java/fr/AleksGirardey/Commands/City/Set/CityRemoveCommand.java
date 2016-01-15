package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.CityCommandAssistant;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityRemoveCommand extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        return CommandResult.empty();
    }
}
