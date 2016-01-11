package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.CityCommandMayor;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

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
        player.sendMessage(Text.of("Can't find '" + newMayor + "' in your citizens"));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int playerCityId = 0;

        playerCityId = Core.getPlayerHandler().getElement(player, "player_cityId");
        Core.getCityHandler().setMayor(
                context.<String>getOne("[resident]").get(),
                playerCityId);
        Core.getBroadcastHandler().cityChannel(playerCityId, context.<String>getOne("[resident]") + " is now the new mayor.");
        return CommandResult.success();
    }
}
