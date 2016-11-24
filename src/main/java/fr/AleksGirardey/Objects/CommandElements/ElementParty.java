package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ElementParty extends CommandElement {
    public ElementParty(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object    parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (commandSource instanceof Player) {
            DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

            if (!Core.getPartyHandler().isLeader(player))
                throw commandArgs.createError(Text.of("you need to be leader of a party"));
            PartyWar    party = Core.getPartyHandler().getPartyFromLeader(player);
            String      name = commandArgs.next();
            for (DBPlayer p : party.toList())
                if (p.getDisplayName().equals(name))
                    return p;
        }
        throw commandArgs.createError(Text.of(commandArgs + " is not a valid member"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (commandSource instanceof Player) {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

            if (Core.getPartyHandler().isLeader(player))
                return Core.getPartyHandler().getPartyFromLeader(player).toList().stream().filter(p -> p != player).map(DBPlayer::getDisplayName).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
