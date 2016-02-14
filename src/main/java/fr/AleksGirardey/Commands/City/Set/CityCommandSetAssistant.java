package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandSetAssistant extends CityCommandSetMayor {

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        boolean         res = true;
        String          uuid = null;

        uuid = context.<String>getOne("[resident]").get();

        if (Core.getCityHandler().<String>getElement(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                "city_playerOwner").equals(uuid))
            res = false;
        return super.SpecialCheck(player, context) && res;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String uuid = Core.getPlayerHandler().getUuidFromName(context.<String>getOne("[resident]").get());
        Core.getPlayerHandler().setElement(
                uuid,
                "player_assistant",
                true);
        Core.getBroadcastHandler().cityChannel(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                context.<String>getOne("[resident]") + " is now assistant.");
        return CommandResult.success();
    }
}
