package fr.craftandconquest.commands.party;

import fr.craftandconquest.commands.Commands;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import fr.craftandconquest.objects.war.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.format.TextColors;

public class                    PartyDelete extends Commands {
    @Override
    protected boolean          SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getPartyHandler().isLeader(player);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        PartyWar party = Core.getPartyHandler().getFromPlayer(player);
        Core.getBroadcastHandler().partyChannel(party, "Le groupe a été dissout.", TextColors.RED);
        Core.getPartyHandler().removeParty(player);
        return CommandResult.success();
    }
}
