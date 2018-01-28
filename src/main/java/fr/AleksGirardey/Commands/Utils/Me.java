package fr.AleksGirardey.Commands.Utils;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Me extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        City    city = player.getCity();
        Faction faction = (city != null ? player.getCity().getFaction() : null);

        player.sendMessage(Text.of(TextColors.DARK_AQUA, ""
                + (faction != null ? ("Faction : " + player.getCity().getFaction().getDisplayName()
                + "\nVille : " + player.getCity().getDisplayName() + "\n") : "")
                + "Solde : " + player.getBalance()
                + "\nPoints : " + player.getScore()));
        return CommandResult.success();
    }
}
