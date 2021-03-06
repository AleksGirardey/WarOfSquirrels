package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class setRecruit extends CityCommandAssistant {
    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer            resident = context.<DBPlayer>getOne("[citizen]").orElse(null);

        resident.setResident(false);
        resident.setAssistant(false);
        Core.getBroadcastHandler().cityChannel(player.getCity(),resident + " est maintenant une recrue.", TextColors.BLUE);
        return CommandResult.success();
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer            newCitizen = context.<DBPlayer>getOne("[citizen]").orElse(null);


        if (newCitizen != null && newCitizen.getCity() == player.getCity() && player.getCity().getOwner() != newCitizen)
            return true;

        player.sendMessage(Text.of(TextColors.RED, newCitizen + " is not a valid citizen.", TextColors.RESET));
        return false;
    }
}
