package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Invitations.PartyWarInvitation;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class                    PartyInvite extends Commands {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer                newOne = Core.getPlayerHandler().get(context.<Player>getOne("[player]").get());


        if (!Core.getPartyHandler().isLeader(player)) {
            player.sendMessage(Text.of("You need to be leader to invite someone"));
            return false;
        }

        if (Core.getPartyHandler().contains(newOne)) {
            player.sendMessage(Text.of("player already belongs to a party"));
            return false;
        }

        if (context.hasAny("<player>")) {
            Collection<Player> newones = context.<Player>getAll("<citizen>");

            for (Player p : newones) {
                DBPlayer    pl = Core.getPlayerHandler().get(p);
                if (Core.getPartyHandler().contains(pl)) {
                    player.sendMessage(Text.of("player already belongs to a party"));
                    return false;
                }
            }
        }
        return  true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newOne = Core.getPlayerHandler().get(context.<Player>getOne("[player]").get());
        PartyWar                party = Core.getPartyHandler().getPartyFromLeader(player);

        Core.getInvitationHandler().createInvitation(new PartyWarInvitation(newOne, player, party));
        Core.getBroadcastHandler().partyInvitation(player, newOne);
        if (context.hasAny("<player>")) {
            Collection<Player>      newOnes = context.<Player>getAll("<player>");

            for (Player p : newOnes) {
                DBPlayer    pl = Core.getPlayerHandler().get(p);
                Core.getBroadcastHandler().partyInvitation(pl, newOne);
                Core.getInvitationHandler().createInvitation(new PartyWarInvitation(pl, player, party));
            }
        }
        return CommandResult.success();
    }
}