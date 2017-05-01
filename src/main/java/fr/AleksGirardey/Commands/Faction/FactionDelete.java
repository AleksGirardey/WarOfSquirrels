package fr.AleksGirardey.Commands.Faction;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

public class FactionDelete extends FactionCommandMayor {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return true; }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        String          fName = player.getCity().getFaction().getDisplayName(), cName = player.getCity().getDisplayName();
        if (Core.getFactionHandler().delete(player.getCity().getFaction())) {
            Core.SendText(Text.of(TextColors.DARK_RED, "La faction ", TextStyles.ITALIC, fName, TextStyles.RESET, " à été dissoute et sa capitale ",
                    TextStyles.ITALIC, cName, TextStyles.RESET, " n'est plus.", TextColors.RESET));
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
