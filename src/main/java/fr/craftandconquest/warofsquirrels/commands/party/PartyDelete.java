package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
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
