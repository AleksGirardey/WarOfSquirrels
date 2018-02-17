package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FactionList extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        player.sendMessage(Text.of(TextColors.BLUE, "----==== faction(s) [" + Core.getFactionHandler().getFactionNameList().size() + "] ====----\n"
                + Utils.toStringFromList(Core.getFactionHandler().getFactionNameList()), TextColors.RESET));
        return CommandResult.success();
    }
}
