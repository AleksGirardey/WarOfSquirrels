package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class            PartyRemove extends Commands {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getPartyHandler().getPartyFromLeader(player) != null;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        PartyWar        party = Core.getPartyHandler().getPartyFromLeader(player);
        DBPlayer        target = context.<DBPlayer>getOne("[player]").get();

        party.remove(target);
        party.SendMessage(Text.of(TextColors.RED, target.getDisplayName() + " à été kick du groupe.", TextColors.RESET));
        target.sendMessage(Text.of(TextColors.RED,"vous avez été kick du groupe.", TextColors.RESET));
        return CommandResult.success();
    }
}
