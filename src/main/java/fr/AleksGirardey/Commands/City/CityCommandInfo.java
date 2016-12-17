package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CityCommandInfo extends CityCommand {

    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return true;
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        if (!context.hasAny("[city]") && player.getCity() == null) {
            Text        message = Text.of("Vous devez appartenir à une ville pour obtenir ses informations. (/city [ville])");
            player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city;
        String                  arg;

        arg = context.<String>getOne("[city]").orElse("");
        if (arg.equals(""))
            city = player.getCity();
        else
            city = Core.getCityHandler().get(arg);

        player.sendMessage(Text.of("---===| " + Core.getInfoCityMap().get(city).getRank().getName() + " " + city.getDisplayName() + "[" + city.getCitizens().size() + "] |===---"));
        player.sendMessage(Text.of("Mayor: " + city.getOwner().getDisplayName()));
        player.sendMessage(Text.of("Assistant(s): " + city.getAssistantsAsString()));
        player.sendMessage(Text.of("Citizens: " + city.getCitizensAsString()));
        player.sendMessage(Text.of("Tag: " + city.getTag()));
        player.sendMessage(Text.of("Chunks [" + Core.getChunkHandler().getSize(city) + "/" + Core.getInfoCityMap().get(city).getRank().getChunkMax() + "]"));
        player.sendMessage(Text.of("Outpost [" + Core.getChunkHandler().getOutpostSize(city) + "]"));
        player.sendMessage(Text.of("Allies: " + Core.getDiplomacyHandler().getAlliesAsString(city)));
        player.sendMessage(Text.of("Enemies: " + Core.getDiplomacyHandler().getEnemiesAsString(city)));
        player.sendMessage(Text.of("Permissions: " + Core.getPermissionHandler().toString(city)));

        return CommandResult.success();
    }
}
