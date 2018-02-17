package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                CityCommandLeave extends CityCommand {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        return !(player.getCity().getOwner() == player);
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        City                city = player.getCity();

        if (player.isAssistant())
            player.setAssistant(false);
        player.setCity(null);
        city.removeCitizen(player);
        Core.getInfoCityMap().get(city).getChannel().removeMember(player.getUser().getPlayer().get());
        Core.getBroadcastHandler().cityChannel(city, player.getDisplayName() + " a quitté la ville.", TextColors.RED);
        player.sendMessage(Text.of(TextColors.RED, "Vous n'appartennez désormais plus à " + city.getDisplayName()));
        return CommandResult.success();
    }
}
