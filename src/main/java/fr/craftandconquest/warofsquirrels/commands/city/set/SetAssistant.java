package fr.craftandconquest.commands.city.set;

import fr.craftandconquest.commands.city.CityCommandMayor;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class SetAssistant extends CityCommandMayor {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getCity().getOwner() != context.<DBPlayer>getOne("[citizen]").get() || player.hasAdminMode();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newAssistant = context.<DBPlayer>getOne("[citizen]").get();

        newAssistant.setAssistant(true);
        Core.getBroadcastHandler().cityChannel(player.getCity(), newAssistant.getDisplayName() + " is now assistant.");
        return CommandResult.success();
    }
}
