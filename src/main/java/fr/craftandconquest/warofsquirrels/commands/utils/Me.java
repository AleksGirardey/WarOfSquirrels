package fr.craftandconquest.warofsquirrels.commands.utils;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
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
                + (faction != null ? ("faction : " + player.getCity().getFaction().getDisplayName()
                + "\nVille : " + player.getCity().getDisplayName() + "\n") : "")
                + "Solde : " + player.getBalance()
                + "\nPoints : " + player.getScore()));
        return CommandResult.success();
    }
}
