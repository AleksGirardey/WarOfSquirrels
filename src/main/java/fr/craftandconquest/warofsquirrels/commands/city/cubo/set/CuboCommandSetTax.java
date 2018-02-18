package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class CuboCommandSetTax extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        List<DBPlayer> list = new ArrayList<>();

        if (Core.getLoanHandler().get(cubo) == null) {
            player.sendMessage(Text.of(TextColors.RED, "Vous devez d'abord louer le cubo à l'aide d'une pancarte", TextColors.RESET));
            return false;
        }

        list.add(cubo.getOwner());
        list.addAll(cubo.getCity().getAssistants());
        list.add(cubo.getOwner());

        if (list.contains(player))
            return true;
        player.sendMessage(Text.of(TextColors.RED, "Vous n'avez pas les droits pour faire ça.",TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();

        cubo.getLoan().setRentPrice(context.<Integer>getOne(Text.of("[price]")).orElse(10));
        cubo.getLoan().actualize();
        return null;
    }
}
