package fr.craftandconquest.warofsquirrels.objects.commandelements;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Cubo;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ElementCubo extends CommandElement {
    public ElementCubo(@Nullable Text key) { super(key); }

    @Override
    protected Object        parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String name = commandArgs.next();

        if (commandSource instanceof Player) {
            Cubo    fromName = Core.getCuboHandler().getFromName(name);

            if (fromName != null) return fromName;
        }
        throw commandArgs.createError(Text.of(TextColors.RED, name + " n'est un nom de cubo valide.", TextColors.RESET));
    }

    @Override
    public List<String>     complete(CommandSource src, CommandArgs args, CommandContext context) {
        if (src instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) src);

            return Core.getCuboHandler().getStringFromPlayer(player);
        }
        return Collections.emptyList();
    }
}
