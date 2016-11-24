package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
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

public class ElementAlly extends CommandElement{
    public ElementAlly(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));
        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);
        City        city = player.getCity();

        if (city != null) {
            List<City>      allies = Core.getDiplomacyHandler().getAllies(city);
            City            c = Core.getCityHandler().get(commandArgs.next());

            if (c != null && allies.contains(c))
                return c.getId();
        }
        throw commandArgs.createError(Text.of(" is not a valid city"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);
        City        city = player.getCity();

        if (city != null)
            return Core.getCityHandler().getAlliesName(city);
        return Collections.emptyList();
    }
}
