package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetHelp extends CityCommand {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        player.sendMessage(Text.of("--==| City set help |==--"));
        player.sendMessage(Text.of("/city set spawn"));
        player.sendMessage(Text.of("/city set mayor [player]"));
        player.sendMessage(Text.of("/city set assistant [player]"));
        return CommandResult.success();
    }
}
