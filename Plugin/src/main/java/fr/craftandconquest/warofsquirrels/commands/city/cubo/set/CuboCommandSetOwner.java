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

public class CuboCommandSetOwner extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();

        List<DBPlayer>  list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (player.hasAdminMode() || list.contains(player))
            return true;

        player.sendMessage(Text.of(TextColors.RED, "Vous n'avez pas le droit de définir le propriétaire de ce cubo.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer target = context.<DBPlayer>getOne(Text.of("[player]")).get();

        cubo.setOwner(target);
        player.sendMessage(Text.of(TextColors.GREEN, "Le propriétaire du cubo ",
                TextColors.GOLD, cubo.getName(),
                TextColors.GREEN, " est maintenant ",
                TextColors.GOLD, target.getDisplayName(), TextColors.RESET));
        target.sendMessage(Text.of(TextColors.GREEN, "Vous êtes maintenant le propriétaire du cubo ",
                TextColors.GOLD, cubo.getName(), TextColors.RESET));
        return null;
    }
}
