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

public class ElementFaction extends CommandElement {
    public ElementFaction(@Nullable Text key) { super(key); }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        String      faction = commandArgs.next();

        if (Core.getFactionHandler().get(faction) == null)
            throw commandArgs.createError(Text.of(faction + " is not a valid faction name."));
        return Core.getFactionHandler().get(faction);
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        return Core.getFactionHandler().getFactionNameList();
    }
}
