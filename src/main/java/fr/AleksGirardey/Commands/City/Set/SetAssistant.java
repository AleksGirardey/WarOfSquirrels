package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandMayor;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class SetAssistant extends CityCommandMayor {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getCity().getOwner() != context.<DBPlayer>getOne("[citizen]").get();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newAssistant = context.<DBPlayer>getOne("[citizen]").get();

        newAssistant.setAssistant(true);
        Core.getBroadcastHandler().cityChannel(player.getCity(), newAssistant.getDisplayName() + " is now assistant.");
        return CommandResult.success();
    }
}
