package fr.AleksGirardey.Commands.Faction.Set;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FactionSetHelp extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of(TextColors.GREEN,
                "--==| Faction set help |==--\n" +
                        "/faction set ally [faction] <build> <container> <switch>\n" +
                        "/faction set enemy [faction] <build> <container> <switch>\n" +
                        "/faction set neutral [faction]\n"));

        return CommandResult.success();
    }
}
