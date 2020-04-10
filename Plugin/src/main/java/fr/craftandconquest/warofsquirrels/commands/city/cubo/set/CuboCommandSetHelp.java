package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CuboCommandSetHelp extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of(TextColors.AQUA, "=== Cubo set ===\n",
                "... set owner : Défini le propriétaire du cubo\n" +
                        "... set inperm : Défini les permissions des joueurs dans la liste\n" +
                        "... set outperm : Défini les permissions des joueurs hors de la liste\n"
        ,TextColors.RESET));
        return CommandResult.success();
    }
}
