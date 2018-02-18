package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class CuboCommandAdd extends Commands {

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer owner, loaner, mayor;
        List<DBPlayer> assistants;

        owner = cubo.getOwner();
        loaner = cubo.getLoan().getLoaner();
        assistants = Core.getCityHandler().getAssistants(cubo.getCity());
        mayor = cubo.getCity().getOwner();

        if (player.hasAdminMode()
                || owner == player
                || loaner == player
                || mayor == player
                || assistants.contains(player))
            return true;

        player.sendMessage(Text.of(TextColors.RED, "Vous ne pouvez pas ajouter quelqu'un à ce cubo.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        DBPlayer member = context.<DBPlayer>getOne(Text.of("[player]")).get();

        cubo.add(member);

        player.sendMessage(Text.of(TextColors.GREEN, "Vous avez ajouté ",
                TextColors.GOLD, member.getDisplayName(),
                TextColors.GREEN, " au cubo ",
                TextColors.GOLD, cubo.getName(),
                TextColors.RESET));
        member.sendMessage(Text.of(TextColors.GREEN, "Vous avez été ajouté en tant que membre du cubo ",
                TextColors.GOLD, cubo.getName(), TextColors.RESET));

        return null;
    }
}
