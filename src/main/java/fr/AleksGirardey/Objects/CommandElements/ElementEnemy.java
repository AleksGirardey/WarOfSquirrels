package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
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

public class ElementEnemy extends CommandElement {
    public ElementEnemy(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object            parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));
        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

        if (player.getCity() != null) {
            Faction         faction = player.getCity().getFaction();
            List<Faction>   enemies = Core.getDiplomacyHandler().getEnemies(faction);
            Faction         f = Core.getFactionHandler().get(commandArgs.next());

            if (f != null && enemies.contains(f))
                return f;
        }
        throw commandArgs.createError(Text.of(" is not a valid"));
    }

    @Override
    public List<String>         complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

        if (player.getCity() != null)
            return Core.getFactionHandler().getEnemiesName(player.getCity().getFaction());
        return Collections.emptyList();
    }
}
