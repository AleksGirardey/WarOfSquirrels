package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class                PartyLeave extends Commands {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getPartyHandler().contains(player) && !Core.getPartyHandler().isLeader(player);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        Core.getPartyHandler().getFromPlayer(player).Send(player.getDisplayName() + " a quitté le groupe.");
        Core.getPartyHandler().getFromPlayer(player).remove(player);
        return CommandResult.success();
    }
}
