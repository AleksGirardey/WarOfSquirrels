package fr.craftandconquest.commands.utils;

import fr.craftandconquest.commands.Commands;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class MoneyRemove extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer joueur = context.<DBPlayer>getOne("[joueur]").get();
        int montant = context.<Integer>getOne("[montant]").get();

        joueur.withdraw(montant);
        player.sendMessage(Text.of(TextColors.GREEN, "Le solde de '" + joueur.getDisplayName() + "' est maintenant de : " + joueur.getBalance(), TextColors.RESET));
        return CommandResult.success();
    }
}
