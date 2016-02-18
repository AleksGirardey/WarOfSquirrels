package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public abstract class SetDiplomacy extends CityCommandAssistant{

    protected abstract void NewDiplomacy(Player player, int cityId2);

    protected void Annouce(int cityId1, int cityId2, String relation) {
        Core.Send("[Diplomacy Alert] " + Core.getCityHandler().<String>getElement(cityId1, "city_displayName")
                + " now treat "
                + Core.getCityHandler().<String>getElement(cityId2, "city_displayName")
                + " as " + relation + ".");
    }

    protected boolean CanDoIt(Player player) {
        if (super.CanDoIt(player))
            return true;
        player.sendMessage(Text.of("You need to belong to a city or you are not enough influent to do diplomacy"));
        return false;
    }

    protected boolean SpecialCheck(Player player, CommandContext context) {
        /*String              cityName = context.<String>getOne("[city]").get();
        Collection<String>  citiesNames = null;
        CityHandler         cityHandler = Core.getCityHandler();

        if (context.hasAny("<city>"))
            citiesNames = context.<String>getAll("<city>");

        if (cityHandler.getCityFromName(cityName) == 0) {
            player.sendMessage(Text.of("City '" + cityName + "' doesn't exist !"));
            return false;
        }
        if (citiesNames != null)
            for (String name : citiesNames)
                if (cityHandler.getCityFromName(name) == 0) {
                    player.sendMessage(Text.of("City '" + name + "' doesn't exist !"));
                    return false;
                }*/
        return true;
    }

    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String                  cityName = context.<String>getOne("[city]").get();
        Collection<String>      citiesNames = null;
        PlayerHandler           playerHandler = Core.getPlayerHandler();
        CityHandler             cityHandler = Core.getCityHandler();
        int                     cityId;

        if (context.hasAny("<city>"))
            citiesNames = context.<String>getAll("<city>");

        cityId = playerHandler.<Integer>getElement(player, "player_cityId");
        NewDiplomacy(player,
            cityHandler.getCityFromName(cityName));
        if (citiesNames != null)
            for (String name : citiesNames)
                NewDiplomacy(
                        player,
                        cityHandler.getCityFromName(name));
        return CommandResult.success();
    }
}
