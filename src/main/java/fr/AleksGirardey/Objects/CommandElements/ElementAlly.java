package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
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

    protected ElementAlly(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (!(commandSource instanceof Player))
            throw commandArgs.createError(Text.of("Only a player can perform this command."));
        Player  player = (Player) commandSource;
        int     cityId = Core.getPlayerHandler().<Integer>getElement(player, GlobalPlayer.cityId), id;

        if (Core.getPlayerHandler().<Integer>getElement(player, GlobalPlayer.cityId) != null) {
            List<Integer>       allies = Core.getCityHandler().getAllies(cityId);
            id = Core.getCityHandler().getCityFromName(commandArgs.next());

            if (id != 0 && allies.contains(id))
                return id;
        }
        throw commandArgs.createError(Text.of(" is not a valid city"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (!(commandSource instanceof Player))
            return Collections.emptyList();
        Player  player = (Player) commandSource;

        if (Core.getPlayerHandler().<Integer>getElement(player, GlobalPlayer.cityId) != null) {
            return Core.getCityHandler().getAlliesName(
                    Core.getPlayerHandler().<Integer>getElement(player, GlobalPlayer.cityId));
        }
        return Collections.emptyList();
    }
}
