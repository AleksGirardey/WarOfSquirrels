package fr.craftandconquest.commands.city.set;

import fr.craftandconquest.commands.city.CityCommandMayor;
import fr.craftandconquest.objects.dbobject.City;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

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
        return CommandResult.success();
    }
}
