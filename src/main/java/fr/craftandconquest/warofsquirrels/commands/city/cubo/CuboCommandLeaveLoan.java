package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import fr.craftandconquest.warofsquirrels.commands.Commands;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CuboCommandLeaveLoan extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();

        if (cubo.getLoan().getLoaner() == player)
            return true;

        player.sendMessage(Text.of(TextColors.RED, "Vous n'êtes pas locataire de ce cubo", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Cubo cubo = context.<Cubo>getOne(Text.of("[cubo]")).get();
        Text message = Text.of(TextColors.GOLD, player.getDisplayName(), TextColors.GREEN, " n'est plus le locataire du cubo ", TextColors.GOLD, cubo.getName(), TextColors.RESET);
        cubo.getLoan().setLoaner(null);
        cubo.getLoan().actualize();

        cubo.getOwner().sendMessage(message);
        if (cubo.getLoan().getPlayer() != null)
            cubo.getLoan().getPlayer().sendMessage(message);
        else {
            cubo.getCity().getOwner().sendMessage(message);
            cubo.getCity().getAssistants().forEach(c -> c.sendMessage(message));
        }

        return CommandResult.success();
    }
}
