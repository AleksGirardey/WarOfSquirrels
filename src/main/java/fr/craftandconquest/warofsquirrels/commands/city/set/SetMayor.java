package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandMayor;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.format.TextColors;

public class SetMayor extends CityCommandMayor {

    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        return context.<DBPlayer>getOne("[citizen]").get().getCity() == player.getCity() || player.hasAdminMode();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city = player.getCity();
        DBPlayer                newMayor = context.<DBPlayer>getOne("[citizen]").get();

        city.setOwner(newMayor);
        Core.getBroadcastHandler().cityChannel(city, newMayor + " est maintenant le nouveau maire de la ville.", TextColors.GOLD);
        return CommandResult.success();
    }
}
