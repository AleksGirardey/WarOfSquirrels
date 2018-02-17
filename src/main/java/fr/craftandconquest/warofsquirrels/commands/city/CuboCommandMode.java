package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class CuboCommandMode extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        if (Core.getCuboHandler().playerExists(player))
            Core.getCuboHandler().deactivateCuboMode(player);
        else
            Core.getCuboHandler().activateCuboMode(player);
        return CommandResult.success();
    }
}
