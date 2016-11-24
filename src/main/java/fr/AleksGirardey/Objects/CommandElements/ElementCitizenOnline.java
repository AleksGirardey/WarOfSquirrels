package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
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
    protected Object        parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        Object              o = super.parseValue(commandSource, commandArgs);
        DBPlayer            player = Core.getPlayerHandler().get((Player) commandSource);
        List<Player>        onlines = Core.getCityHandler().getOnlinePlayers(player.getCity());

        for (Player p : onlines) {
            DBPlayer    pl = Core.getPlayerHandler().get(p);

            if (pl.getDisplayName().equals(o))
                return p;
        }
        throw commandArgs.createError(Text.of("You need to invite an online player from your city"));
    }

    @Override
    public List<String>     complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        List<String>        list = super.complete(commandSource, commandArgs, commandContext);
        List<String>        res = new ArrayList<>();
        List<Player>        onlines;
        String              name;
        DBPlayer            player = Core.getPlayerHandler().get((Player) commandSource);

        if (list.isEmpty())
            return list;
        onlines = Core.getCityHandler().getOnlinePlayers(player.getCity());
        for (Player p : onlines) {
            DBPlayer    pl = Core.getPlayerHandler().get(p);
            if (list.contains(pl.getDisplayName()))
                list.add(pl.getDisplayName());
        }
        return res;
    }
}