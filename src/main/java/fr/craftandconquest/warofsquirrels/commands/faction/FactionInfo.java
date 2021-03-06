package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class FactionInfo extends Commands {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        Text                message;


        if (!context.hasAny("<faction>") && player.getCity() == null) {
            message = Text.of("Vous devez appartenir a une faction pour effectuer cette commande sans argument. /faction info <faction>");
            player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Faction             faction;

        if (context.hasAny("<faction>"))
            faction = context.<Faction>getOne("<faction>").orElse(null);
        else
            faction = player.getCity().getFaction();

        if (faction == null)
            return CommandResult.empty();

        player.sendMessage(Text.of(Core.getInfoFactionMap().get(faction).getColor(),
                "--==| " + Core.getInfoFactionMap().get(faction).getRank().getName() + " " + faction.getDisplayName() + " [ " + faction.getSize() + "] |==--\n"
                        + "[" + Core.getInfoFactionMap().get(faction).getRank().getPrefixMayor() + "] " + faction.getCapital().getOwner().getDisplayName() + "\n"
                        + "[Capitale] " + faction.getCapital().getDisplayName() + "\n"
                        + "[Vassaux] " + faction.getCitiesAsString()  + "\n"
                        + "[Allies] " + Core.getDiplomacyHandler().getAlliesAsString(faction) + "\n"
                        + "[Enemies] " + Core.getDiplomacyHandler().getEnemiesAsString(faction) + "\n"
        ));
        return CommandResult.success();
    }
}
