package fr.craftandconquest.warofsquirrels.commands.city.cubo;

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

public class CuboCommandRemove extends Commands {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer member = context.<DBPlayer>getOne(Text.of("[member]")).get();
        List<DBPlayer> list = new ArrayList<>();

        list.addAll(cubo.getCity().getAssistants());
        list.add(cubo.getCity().getOwner());
        list.add(cubo.getLoan().getLoaner());

        if (!list.contains(player)) {
            player.sendMessage(Text.of(TextColors.RED, "Vous n'avez pas les droits pour faire ça.", TextColors.RESET));
            return false;
        }

        if (cubo.getInList().contains(member))
            return true;

        player.sendMessage(Text.of(TextColors.RED, "Ce joueur n'appartient pas à ce cubo.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer member = context.<DBPlayer>getOne(Text.of("[member]")).get();

        cubo.getInList().remove(member);
        return CommandResult.success();
    }
}
