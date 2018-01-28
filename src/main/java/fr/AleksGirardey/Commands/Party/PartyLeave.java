package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                PartyLeave extends Commands {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        if (Core.getPartyHandler().isLeader(player))
            player.sendMessage(Text.of(TextColors.RED, "You can't leave the party if you are the leader. Delete it with /party delete."));
        return Core.getPartyHandler().contains(player) && !Core.getPartyHandler().isLeader(player);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        PartyWar party = Core.getPartyHandler().getFromPlayer(player);
        Core.getPartyHandler().getFromPlayer(player).remove(player);
        Core.getBroadcastHandler().partyChannel(party, player.getDisplayName() + " a quitté le groupe.", TextColors.RED);
        player.sendMessage(Text.of(TextColors.RED, "Vous avez quitté votre groupe.", TextColors.RESET));
        return CommandResult.success();
    }
}
