package fr.craftandconquest.warofsquirrels.commands.war;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.database.GlobalWar;
import fr.craftandconquest.warofsquirrels.objects.war.War;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class WarTarget extends Commands {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        War             war = Core.getWarHandler().getWar(player);
        return          war.getPhase().equals(GlobalWar.preparationPhase) && war.getDefender().equals(player.getCity());
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getWarHandler().getWar(player).setTarget(context.<DBPlayer>getOne("[player]").orElseGet(null));
        return null;
    }
}
