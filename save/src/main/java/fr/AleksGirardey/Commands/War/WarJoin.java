package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.War.War;
=======
import fr.AleksGirardey.Objects.War;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class WarJoin extends CityCommand {
    @Override
    protected boolean   SpecialCheck(Player player, CommandContext context) {
        War             war = context.<War>getOne("[ally]").get();
        int             cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return Core.getCityHandler().areAllies(war.getAttacker(), cityId)
                || Core.getCityHandler().areAllies(war.getDefender(), cityId)
                || war.getAttacker() == cityId
                || war.getDefender() == cityId;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int         cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        War         war = context.<War>getOne("[ally]").get();

        if (Core.getCityHandler().areAllies(war.getAttacker(), cityId)
                || cityId == war.getAttacker())
            war.addAttacker(player);
        else if (Core.getCityHandler().areAllies(war.getDefender(), cityId)
                || cityId == war.getDefender())
            war.addDefender(player);
        return CommandResult.success();
    }
}