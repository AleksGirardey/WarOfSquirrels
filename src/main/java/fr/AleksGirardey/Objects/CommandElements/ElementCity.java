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

public class ElementCity extends CommandElement {
    public ElementCity(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String      city = commandArgs.next();

        if (!Core.getCityHandler().getCityNameList().contains(city))
            throw commandArgs.createError(Text.of(city + " is not a valid city name."));
        return city;
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        return Core.getCityHandler().getCityNameList();
    }

    public Text getUsage(CommandSource source) {
        return Text.of("[city]");
    }
}
