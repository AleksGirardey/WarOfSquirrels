package fr.AleksGirardey.Commands.Faction;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FactionInfo extends Commands {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        Text                message;


        if (context.hasAny("<faction>")) {
            if (Core.getFactionHandler().get(context.<String>getOne("<faction>").get()) != null)
                return true;
            message = Text.of(context.<String>getOne("<faction>") + " doesn't exist.");
        } else if (player.getCity() != null)
            return true;
        message = Text.of("You need to belongd to a faction");
        player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Faction             faction = player.getCity().getFaction();

        if (context.hasAny("<faction>"))
            faction = Core.getFactionHandler().get(context.<String>getOne("<faction>").get());

        player.sendMessage(Text.of(Core.getInfoFactionMap().get(faction).getColor(),
                "--==| " + Core.getInfoFactionMap().get(faction).getRank().getName() + faction.getDisplayName() + " [ " + faction.getSize() + "] |==--\n"
                        + "[" + Core.getInfoFactionMap().get(faction).getRank().getPrefixMayor() + "] " + faction.getCapital().getOwner().getDisplayName() + "\n"
                        + "[Capitale] " + faction.getCapital().getDisplayName() + "\n"
                        + "[Vassaux] " + faction.getCitiesAsString()  + "\n"
                        + "[Allies] " + Core.getDiplomacyHandler().getAlliesAsString(faction) + "\n"
                        + "[Enemies] " + Core.getDiplomacyHandler().getEnemiesAsString(faction) + "\n"
        ));
        return CommandResult.success();
    }
}
