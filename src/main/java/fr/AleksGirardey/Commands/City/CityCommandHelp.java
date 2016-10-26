package fr.AleksGirardey.Commands.City;

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandHelp extends CityCommand {
    @Override
    protected boolean CanDoIt(Player player) {
        return true;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        player.sendMessage(Text.of("--==| City help |==--"));
        player.sendMessage(Text.of("/city create <name>"));
        player.sendMessage(Text.of("/city info [name]"));
        player.sendMessage(Text.of("/city claim"));
        player.sendMessage(Text.of("/city unclaim"));
        player.sendMessage(Text.of("/city set ..."));
        return CommandResult.success();
    }
}
