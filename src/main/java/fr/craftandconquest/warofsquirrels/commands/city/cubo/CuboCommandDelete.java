package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CuboCommandDelete extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer owner = cubo.getOwner();

        if (player.hasAdminMode() || owner == player)
            return true;
        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas supprimer ce Cubo", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();

        Core.getCuboHandler().delete(cubo);
        player.sendMessage(Text.of(TextColors.GREEN, "Le cubo ",
                TextColors.GOLD, cubo.getName(),
                TextColors.GREEN, " est maintenant détruit",
                TextColors.RESET));
        return CommandResult.success();
    }
}
