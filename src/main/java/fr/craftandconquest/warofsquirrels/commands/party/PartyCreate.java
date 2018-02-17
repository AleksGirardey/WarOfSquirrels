package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class                PartyCreate extends Commands {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        if (!Core.getPartyHandler().contains(player))
            return true;
        player.sendMessage(Text.of("Leave your party before creating a new one"));
        return false;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        Core.getPartyHandler().addParty(new PartyWar(player));
        player.sendMessage(Text.of("Your party have been created"));
        return CommandResult.success();
    }
}
