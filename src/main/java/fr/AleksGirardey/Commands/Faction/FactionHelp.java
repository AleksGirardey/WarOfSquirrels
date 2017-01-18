package fr.AleksGirardey.Commands.Faction;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FactionHelp extends FactionCommand {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return true; }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of(TextColors.GREEN,
                "--==| Faction help |==--\n" +
                        "/faction info <faction>\n" +
                        "/faction create [name] [cityName]\n" +
                        "/faction delete\n" +
                        "/faction set ..."));
        return CommandResult.success();
    }
}