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
        boolean         res = true;

        if (player.getCity().getOwner() == Core.getPlayerHandler().getFromName(context.<String>getOne("[resident]").get()))
            res = false;

        return res;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newAssistant = Core.getPlayerHandler().getFromName(context.<String>getOne("[resident]").get());

        newAssistant.setAssistant(true);
        Core.getBroadcastHandler().cityChannel(player.getCity(), player.getDisplayName() + " is now assistant.");
        return CommandResult.success();
    }
}
