package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CityCommandHelp extends CityCommand {
    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return true;
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of(TextColors.GREEN,
                "--==| City help |==--\n" +
                "/city create [name]\n" +
                "/city info <name>\n" +
                "/city claim\n" +
                "/city unclaim\n" +
                "/city set ..."));
        return CommandResult.success();
    }
}
