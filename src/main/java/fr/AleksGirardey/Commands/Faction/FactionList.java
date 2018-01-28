package fr.AleksGirardey.Commands.Faction;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
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
        player.sendMessage(Text.of(TextColors.BLUE, "----==== Faction(s) [" + Core.getFactionHandler().getFactionNameList().size() + "] ====----\n"
                + Utils.toStringFromList(Core.getFactionHandler().getFactionNameList()), TextColors.RESET));
        return CommandResult.success();
    }
}
