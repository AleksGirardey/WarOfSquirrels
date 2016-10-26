package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
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
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));
        Player player = (Player) commandSource;

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            List<Integer>   enemies = Core.getCityHandler().getEnemies(
                    Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));
            int             id = Core.getCityHandler().getCityFromName(commandArgs.next());

            if (id != 0 && enemies.contains(id))
                return id;
        }
        throw commandArgs.createError(Text.of(" is not a valid"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        Player player = (Player) commandSource;

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            return Core.getCityHandler().getEnemiesName(
                    Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));
        }
        return Collections.emptyList();
    }
}
