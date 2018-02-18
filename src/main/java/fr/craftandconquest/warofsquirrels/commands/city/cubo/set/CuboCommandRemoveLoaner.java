package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.List;

public class CuboCommandRemoveLoaner extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        List<DBPlayer>  list = new ArrayList<>();
        Cubo            cubo = context.<Cubo>getOne(Text.of("[cubo]")).orElse(null);

        if (cubo == null || cubo.getLoan() == null) {
            player.sendMessage(Text.of(TextColors.RED, "Le cubo n'est pas "));
            return false;
        }

        list.add(cubo.getOwner());
        list.add(cubo.getLoan().getPlayer());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;
        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas effectué cette action", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return null;
    }
}
