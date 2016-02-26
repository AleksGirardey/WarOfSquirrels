package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Invitations.PartyWarInvitation;
import fr.AleksGirardey.Objects.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class PartyInvite extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Player newone = context.<Player>getOne("[citizen]").get();

        if (!Core.getPartyHandler().isLeader(player)) {
            player.sendMessage(Text.of("You need to be leader to invite someone"));
            return false;
        }
        if (Core.getPartyHandler().contains(newone)) {
            player.sendMessage(Text.of("player already belongs to a party"));
            return false;
        }

        if (context.hasAny("<citizen>")) {
            Collection<Player> newones = context.<Player>getAll("<citizen>");

            for (Player p : newones) {
                if (Core.getPartyHandler().contains(p)) {
                    player.sendMessage(Text.of("player already belongs to a party"));
                    return false;
                }
            }
        }
        return  true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Player      newOne = context.<Player>getOne("[citizen]").get();
        PartyWar    party = Core.getPartyHandler().getPartyFromLeader(player);

        Core.getInvitationHandler().createInvitation(new PartyWarInvitation(newOne, player, party));
        Core.getBroadcastHandler().partyInvitation(player, newOne);
        if (context.hasAny("<citizen>")) {
            Collection<Player>      newOnes = context.<Player>getAll("<citizen>");

            for (Player p : newOnes) {
                Core.getBroadcastHandler().partyInvitation(player, newOne);
                Core.getInvitationHandler().createInvitation(new PartyWarInvitation(p, player, party));
            }
        }
        return CommandResult.success();
    }
}