package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommand;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class SetHelp extends CityCommand {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of("--==| city set help |==--"));
        player.sendMessage(Text.of("/city set spawn"));
        player.sendMessage(Text.of("/city set mayor [player]"));
        player.sendMessage(Text.of("/city set assistant [player]"));
        return CommandResult.success();
    }
}
