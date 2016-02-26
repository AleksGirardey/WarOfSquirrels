package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.PartyWar;
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

public class ElementParty extends CommandElement {
    public ElementParty(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource commandSource, CommandArgs commandArgs) throws ArgumentParseException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            if (!Core.getPartyHandler().isLeader(player))
                throw commandArgs.createError(Text.of("you need to be leader of a party"));
            PartyWar    party = Core.getPartyHandler().getPartyFromLeader(player);
            String      name = commandArgs.next();
            for (Player p : party.toList())
                if (Core.getPlayerHandler().<String>getElement(p, "player_displayName").equals(name))
                    return p;
        }
        throw commandArgs.createError(Text.of(commandArgs + " is not a valid member"));
    }

    @Override
    public List<String> complete(CommandSource commandSource, CommandArgs commandArgs, CommandContext commandContext) {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;

            if (Core.getPartyHandler().isLeader(player)) {
                List<String> players = new ArrayList<>();

                for (Player p : Core.getPartyHandler().getPartyFromLeader(player).toList())
                    if (p != player)
                        players.add(Core.getPlayerHandler().<String>getElement(p, "player_displayName"));
                return players;
            }
        }
        return Collections.emptyList();
    }
}
