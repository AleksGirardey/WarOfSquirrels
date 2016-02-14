package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandRemove extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        String name = context.<String>getOne("[citizen]").get();
        int     id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return  (Core.getPlayerHandler().getUuidFromName(name)
                .equals(Core.getCityHandler().<String>getElement(id, "city_playerOwner")));
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Core.getPlayerHandler().setElement(
                Core.getPlayerHandler().getUuidFromName(context.<String>getOne("[citizen]").get()),
                "player_cityId",
                0);
        return CommandResult.success();
    }
}
