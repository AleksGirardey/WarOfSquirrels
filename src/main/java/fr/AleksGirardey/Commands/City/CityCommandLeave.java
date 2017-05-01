package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.City.CityCommand;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

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
        Core.getBroadcastHandler().cityChannel(city, player.getDisplayName() + " à quitté la ville.");
        return CommandResult.success();
    }
}
