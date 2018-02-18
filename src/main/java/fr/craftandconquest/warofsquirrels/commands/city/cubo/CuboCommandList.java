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

public class CuboCommandList extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        List<Cubo> cubos = Core.getCuboHandler().getFromPlayer(player);
        Text        message = Text.of("");

        cubos.forEach(c -> message.concat(c.toText().concat(Text.NEW_LINE)));

        player.sendMessage(Text.of(TextColors.AQUA,
                "=== List de cubo [" + cubos.size() + "] ===\n",
                message,
                TextColors.RESET));
        return null;
    }
}
