package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class                    WarJoin extends CityCommand {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        War                     war = context.<War>getOne("[ally]").get();
        City                    city = player.getCity();

        return Core.getCityHandler().areAllies(war.getAttacker(), city)
                || Core.getCityHandler().areAllies(war.getDefender(), city)
                || war.getAttacker() == city
                || war.getDefender() == city;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city = player.getCity();
        War                     war = context.<War>getOne("[ally]").get();

        if (Core.getCityHandler().areAllies(war.getAttacker(), city)
                || city == war.getAttacker())
            war.addAttacker(player);
        else if (Core.getCityHandler().areAllies(war.getDefender(), city)
                || city == war.getDefender())
            war.addDefender(player);
        return CommandResult.success();
    }
}