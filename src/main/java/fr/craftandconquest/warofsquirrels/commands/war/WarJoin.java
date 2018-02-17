package fr.craftandconquest.commands.war;

import fr.craftandconquest.commands.city.CityCommand;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.City;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import fr.craftandconquest.objects.war.War;
import fr.craftandconquest.commands.city.CityCommand;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class                    WarJoin extends CityCommand {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        War                     war = context.<War>getOne("[ally]").get();
        City                    city = player.getCity();

        return Core.getFactionHandler().areAllies(war.getAttacker().getFaction(), city.getFaction())
                || Core.getFactionHandler().areAllies(war.getDefender().getFaction(), city.getFaction())
                || war.getAttacker().getFaction() == city.getFaction()
                || war.getDefender().getFaction() == city.getFaction()
                || war.getAttacker() == city
                || war.getDefender() == city;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city = player.getCity();
        War                     war = context.<War>getOne("[ally]").get();

        if (Core.getFactionHandler().areAllies(war.getAttacker().getFaction(), city.getFaction())
                || city == war.getAttacker() || city.getFaction() == war.getAttacker().getFaction())
            war.addAttacker(player);
        else if (Core.getFactionHandler().areAllies(war.getDefender().getFaction(), city.getFaction())
                || city == war.getDefender() || city.getFaction() == war.getDefender().getFaction())
            war.addDefender(player);
        return CommandResult.success();
    }
}