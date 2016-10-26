package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ElementCitizenOnline extends ElementCitizen {
    public ElementCitizenOnline(@Nullable Text key) {
        super(key);
    }

    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        Object o = super.parseValue(commandSource, commandArgs);
        Player  player = (Player) commandSource;

        List<Player> onlines = Core.getCityHandler().getOnlinePlayers(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"));

        for (Player p : onlines) {
            if (Core.getPlayerHandler().<String>getElement(p, "player_displayName").equals(o))
                return p;
        }
        throw commandArgs.createError(Text.of("You need to invite an online player from your city"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        List<String>        list = super.complete(commandSource, commandArgs, commandContext);
        List<String>        res = new ArrayList<>();
        List<Player>        onlines;
        String              name;

        if (list.isEmpty())
            return list;
        onlines = Core.getCityHandler().getOnlinePlayers(Core.getPlayerHandler().<Integer>getElement((Player) commandSource, "player_cityId"));
        for (Player p : onlines) {
            name = Core.getPlayerHandler().<String>getElement(p, "player_displayName");
            if (list.contains(name))
                list.add(name);
        }
        return res;
    }
}
