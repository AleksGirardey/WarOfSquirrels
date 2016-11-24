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

public class ElementCitizen extends CommandElement {
    public ElementCitizen(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String      name = commandArgs.next();

        if (commandSource instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

            if (player.getCity() != null && Core.getCityHandler()
                    .getCitizensList(player.getCity())
                    .contains(name))
                return name;
        }
        throw commandArgs.createError(Text.of(name + " is not a valid citizen"));
    }

    @Override
    public List<String>         complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (commandSource instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

            if (player.getCity() != null)
                return Core.getCityHandler().getCitizensList(player.getCity());
        }
        return Collections.emptyList();
    }
}
