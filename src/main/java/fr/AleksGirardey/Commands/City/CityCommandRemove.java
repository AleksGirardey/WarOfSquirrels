package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.data.value.mutable.CollectionValue;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;

public class CityCommandRemove extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        String name = context.<String>getOne("[citizen]").get();
        int     id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return  (!Core.getPlayerHandler().getUuidFromName(name)
                .equals(Core.getCityHandler().<String>getElement(id, "city_playerOwner")));
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String citizen = context.<String>getOne("[citizen]").get();

        Core.getPlayerHandler().setElement(
                Core.getPlayerHandler().getUuidFromName(context.<String>getOne("[citizen]").get()),
                "player_cityId",
                null);
        Core.getBroadcastHandler().cityChannel(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                citizen  + " has been kicked from the city");
        //Core.getBroadcastHandler().kickMessage(citizen);
        if (context.hasAny("<citizen>")) {
            Collection<String>      oldCitizens = context.<String>getAll("<citizen>");

            for (String p : oldCitizens) {
                Core.getPlayerHandler().setElement(
                        Core.getPlayerHandler().getUuidFromName(p), "player_cityId", null);
                Core.getBroadcastHandler().cityChannel(
                        Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                        p + " has been kicked from the city");
                //Core.getBroadcastHandler().kickMessage(citizen);
            }
        }

        return CommandResult.success();
    }
}
