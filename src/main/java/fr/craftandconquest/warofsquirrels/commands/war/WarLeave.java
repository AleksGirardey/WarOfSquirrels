package fr.craftandconquest.warofsquirrels.commands.war;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommand;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.War;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    WarLeave extends CityCommand {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getWarHandler().Contains(player);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        War war = Core.getWarHandler().getWar(player);

        if (war.removePlayer(player))
            return CommandResult.success();
        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas quitter la guerre en cours.", TextColors.RESET));
        return CommandResult.empty();
    }
}
