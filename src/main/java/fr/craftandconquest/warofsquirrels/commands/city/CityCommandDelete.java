package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    CityCommandDelete extends CityCommandMayor {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        if (player.getCity().getFaction().getCapital().equals(player.getCity())) {
            player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas supprimer la capitale de votre faction.", TextColors.RESET));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city = player.getCity();

        Core.getCityHandler().delete(city);
        Text message = Text.of("[BREAKING NEWS] " + city.getDisplayName() + " has fallen !");
        Core.sendText(Text.of(TextColors.GOLD, message, TextColors.RESET));
        return CommandResult.success();
    }
}
