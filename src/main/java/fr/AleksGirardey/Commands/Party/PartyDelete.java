package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class                    PartyDelete extends CityCommandAssistant {
    @Override
    protected boolean          SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getPartyHandler().isLeader(player);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        Core.getPartyHandler().removeParty(player);
        return CommandResult.success();
    }
}
