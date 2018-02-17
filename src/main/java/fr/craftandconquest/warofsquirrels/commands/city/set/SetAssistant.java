package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandMayor;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.format.TextColors;

public class SetAssistant extends CityCommandMayor {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getCity().getOwner() != context.<DBPlayer>getOne("[citizen]").get() || player.hasAdminMode();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newAssistant = context.<DBPlayer>getOne("[citizen]").get();

        newAssistant.setAssistant(true);
        Core.getBroadcastHandler().cityChannel(player.getCity(), newAssistant.getDisplayName() + " est maintenant assistant.", TextColors.GOLD);
        return CommandResult.success();
    }
}
