package fr.AleksGirardey.Commands.CityCommand.Set;

import fr.AleksGirardey.Commands.CityCommand.CityCommandMayor;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandSetMayor extends CityCommandMayor {

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        String      newMayor = null;
        String      [][]citizens = null;

        newMayor =  context.<String>getOne("[resident]").get();

        citizens = Core.getCityHandler().getCitizens(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));

        for (String[] citizen : citizens)
            if (citizen[1].equals(newMayor))
                return true;
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int playerCityId = 0;

        playerCityId = Core.getPlayerHandler().getElement(player, "player_cityId");
        Core.getCityHandler().setMayor(
                context.<String>getOne("[resident]").get(),
                playerCityId);
        return CommandResult.success();
    }
}
