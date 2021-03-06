package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
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
            Text message = Text.of("Vous devez appartenir à une ville pour obtenir ses informations. (/city [ville])");
            player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city;
        Text                    text;

        city = context.<City>getOne("[city]").orElse(player.getCity());

        if (city == null) return CommandResult.empty();

        text = Text.of("---===| " + Core.getInfoCityMap().get(city).getCityRank().getName() + " " + city.getDisplayName() + " [" + city.getCitizens().size() + "] |===---\n"
                + "faction: " + city.getFaction().getDisplayName() + "\n"
                + "Mayor: " + city.getOwner().getDisplayName() + "\n"
                + "Assistant(s): " + city.getAssistantsAsString() + "\n"
                + "Resident: " + city.getResidentsInfo() + "\n"
                + "Recruits: " + city.getRecruitsInfo() + "\n"
                + "Tag: " + city.getTag() + "\n"
                + "Chunks [" + Core.getChunkHandler().getSize(city) + "/" + Core.getInfoCityMap().get(city).getCityRank().getChunkMax() + "]\n"
                + "Outpost [" + Core.getChunkHandler().getOutpostSize(city) + "]\n"
                + "permissions: " + Core.getPermissionHandler().toString(city));

        player.sendMessage(Text.of(Core.getInfoCityMap().get(city).getColor(), text, TextColors.RESET));
        return CommandResult.success();
    }
}
