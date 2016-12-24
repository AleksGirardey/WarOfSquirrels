package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ElementDBPlayer extends CommandElement {
    public ElementDBPlayer(@Nullable Text key) { super(key); }

    @Nullable
    @Override
    protected Object    parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String          name = commandArgs.next();

        if (commandSource instanceof Player) {
            DBPlayer    player = Core.getPlayerHandler().getFromName(name);

            if (player != null)
                return player;
        }
        throw commandArgs.createError(Text.of(name + " is not a valid player name"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (commandSource instanceof Player)
            return Core.getPlayerHandler().getStringList();
        return Collections.emptyList();
    }
}