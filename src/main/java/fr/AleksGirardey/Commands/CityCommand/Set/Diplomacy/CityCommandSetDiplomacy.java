package fr.AleksGirardey.Commands.CityCommand.Set.Diplomacy;

import fr.AleksGirardey.Commands.CityCommand.CityCommand;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;
import java.util.Collection;

public abstract class CityCommandSetDiplomacy extends CityCommand{

    protected abstract void NewDiplomacy(int cityId1, int cityId2);

    protected boolean CanDoIt(Player player) {
        PlayerHandler   playerHandler = Core.getPlayerHandler();

        player.sendMessage(Text.of("[CityCommandSetDiplomacy] CanDoIt"));
        try {
            if (playerHandler.getCity(player) != 0
                    && (playerHandler.isOwner(player)
                    /* || playerHandler.isAssistant(player)*/))
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        player.sendMessage(Text.of("You need to belong to a city or you are not enough influent to do diplomacy"));
        return false;
    }

    protected boolean SpecialCheck(Player player, CommandContext context) {
        String              cityName = context.<String>getOne("[city]").get();
        Collection<String>  citiesNames = null;
        CityHandler         cityHandler = Core.getCityHandler();

        player.sendMessage(Text.of("[CityCommandSetDiplomacy] SpecialCheck"));
        if (context.hasAny("<city>"))
            citiesNames = context.<String>getAll("<city>");

        try {
            if (cityHandler.getCityFromName(cityName) == 0) {
                player.sendMessage(Text.of("City '" + cityName + "' doesn't exist !"));
                return false;
            }
            if (citiesNames != null)
                for (String name : citiesNames)
                    if (cityHandler.getCityFromName(name) == 0) {
                        player.sendMessage(Text.of("City '" + name + "' doesn't exist !"));
                        return false;
                    }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String                  cityName = context.<String>getOne("[city]").get();
        Collection<String>      citiesNames = null;
        PlayerHandler           playerHandler = Core.getPlayerHandler();
        CityHandler             cityHandler = Core.getCityHandler();
        int                     cityId;

        player.sendMessage(Text.of("[CityCommandSetDiplomacy] ExecCommand"));
        if (context.hasAny("<city>"))
            citiesNames = context.<String>getAll("<city>");
        try {
            cityId = playerHandler.getCity(player);
            NewDiplomacy(
                    cityId,
                    cityHandler.getCityFromName(cityName));
            if (citiesNames != null)
                for (String name : citiesNames)
                    NewDiplomacy(
                            cityId,
                            cityHandler.getCityFromName(name)
                    );
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return CommandResult.success();
    }
}
