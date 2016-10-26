package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.Utilitaires.Utils;
=======
import fr.AleksGirardey.Objects.Utils;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
<<<<<<< HEAD
import org.spongepowered.api.text.format.TextColors;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

public class CityCommandInfo extends CityCommand {

    @Override
    protected boolean CanDoIt(Player player) {
        return true;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
<<<<<<< HEAD
        if (!context.hasAny("[city]") && Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") == null) {
            Text        message = Text.of("Vous devez appartenir à une ville pour obtenir ses informations. (/city [ville])");
            player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
            return false;
        }
        return true;
=======
/*        String      arg = null;

        arg = context.<String>getOne("[city]").orElse("");

        if (arg.equals("")) {
            if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null)
                return true;
            else
                player.sendMessage(Text.of("You need to specify a city."));
        }
        else {
            if (Core.getCityHandler().getCityFromName(arg) != 0)
                return true;
            else
                player.sendMessage(Text.of("City '" + arg + "' does not exist."));
        }

        return false;*/
        return  true;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        CityHandler     cih = Core.getCityHandler();
        PlayerHandler   plh = Core.getPlayerHandler();
        String      arg = null;
        int         city = 0;

        arg = context.<String>getOne("[city]").orElse("");
        if (arg.equals(""))
            city = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        else
            city = Core.getCityHandler().getCityFromName(arg);

<<<<<<< HEAD
        player.sendMessage(Text.of("---===| " + Core.getInfoCityMap().get(city).getRank().getName() + " " + cih.getElement(city, "city_displayName") + "[" + cih.getCitizens(city).length + "] |===---"));
=======
        player.sendMessage(Text.of("---===| " + cih.getElement(city, "city_displayName") + "[" + cih.getCitizens(city).length + "] |===---"));
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        player.sendMessage(Text.of("Mayor: " + plh.<String>getElement(cih.<String>getElement(city, "city_playerOwner"), "player_displayName")));
        player.sendMessage(Text.of("Assistant(s): " + Utils.getStringFromUuidList(Core.getCityHandler().getAssistants(city))));
        player.sendMessage(Text.of("Citizens: " + Utils.getListFromTableString(cih.getCitizens(city), 1)));
        player.sendMessage(Text.of("Tag: " + cih.getElement(city, "city_tag")));
<<<<<<< HEAD
        player.sendMessage(Text.of("Chunks [" + Core.getChunkHandler().getSize(city) + "/" + Core.getInfoCityMap().get(city).getRank().getChunkMax() + "]"));
        player.sendMessage(Text.of("Outpost [" + Core.getChunkHandler().getOutpostSize(city) + "]"));
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        player.sendMessage(Text.of("Allies: " + Utils.getListFromTableString(cih.getDiplomacy(city, true))));
        player.sendMessage(Text.of("Enemies: " + Utils.getListFromTableString(cih.getDiplomacy(city, false))));
        player.sendMessage(Text.of("Permissions: " + Core.getPermissionHandler().getString(city)));

        return CommandResult.success();
    }
}
