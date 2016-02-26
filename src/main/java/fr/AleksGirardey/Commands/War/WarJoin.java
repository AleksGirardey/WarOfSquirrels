package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.War;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class WarJoin extends CityCommand {
    @Override
    protected boolean   SpecialCheck(Player player, CommandContext context) {
        War             war = context.<War>getOne("[ally]").get();
        int             cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return Core.getCityHandler().areAllies(war.getAttacker(), cityId) || Core.getCityHandler().areAllies(war.getDefender(), cityId);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int         cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        War         war = context.<War>getOne("[ally]").get();

        if (Core.getCityHandler().areAllies(war.getAttacker(), cityId))
            war.addAttacker(player);
        else if (Core.getCityHandler().areAllies(war.getDefender(), cityId))
            war.addDefender(player);
        return CommandResult.success();
    }
}