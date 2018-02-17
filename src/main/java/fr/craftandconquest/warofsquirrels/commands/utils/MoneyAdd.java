package fr.craftandconquest.commands.utils;

import fr.craftandconquest.commands.Commands;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.City;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MoneyAdd extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        int montant = context.<Integer>getOne("[montant]").get();

        if (context.hasAny("[city]")) {
            City city = context.<City>getOne("[city]").get();

            city.insert(montant);
            player.sendMessage(Text.of(TextColors.GREEN, "Le solde de '" + city.getDisplayName() + "' est maintenant de : " + city.getDisplayName(), TextColors.RESET));
            Core.getBroadcastHandler().cityChannel(city, "Le solde de la ville est désormais de " + city.getBalance(), TextColors.GREEN);
        } else {
            DBPlayer joueur = context.<DBPlayer>getOne("[joueur]").get();

            joueur.insert(montant);
            player.sendMessage(Text.of(TextColors.GREEN, "Le solde de '" + joueur.getDisplayName() + "' est maintenant de : " + joueur.getBalance(), TextColors.RESET));
            joueur.sendMessage(Text.of(TextColors.GREEN, "Votre solde est maintenant de " + joueur.getBalance(), TextColors.RESET));
        }
        return CommandResult.success();
    }
}
