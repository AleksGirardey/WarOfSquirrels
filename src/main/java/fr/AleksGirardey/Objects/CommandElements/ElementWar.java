package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.List;

public class ElementWar extends CommandElement {
    public ElementWar(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String      city = commandArgs.next();
        int         cityId = Core.getCityHandler().getCityFromName(city);

        if (cityId != 0 && Core.getWarHandler().Contains(cityId))
            return Core.getWarHandler().getWar(cityId);
        throw commandArgs.createError(Text.of(commandArgs.next() + " is not a valid city name or the city doesn't take an active part to a war."));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        return Core.getWarHandler().getCitiesList();
    }
}
