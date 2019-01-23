package fr.craftandconquest.warofsquirrels.commands.faction.set;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
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
                "--==| faction set help |==--\n" +
                        "/faction set ally [faction] <build> <container> <switch>\n" +
                        "/faction set enemy [faction] <build> <container> <switch>\n" +
                        "/faction set neutral [faction]\n"));

        return CommandResult.success();
    }
}
