package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Invitations.CityInvitation;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class CityCommandAdd extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer    newCitizen = Core.getPlayerHandler().get(context.<Player>getOne("[player]").get());
        boolean     ret = true;

        if (newCitizen.getCity() != null) {
            player.sendMessage(Text.of(newCitizen.getDisplayName() + " already belongs to a city."));
            ret = false;
        }

        if (context.hasAny("<player>")) {
            Collection<Player>  players = context.<Player>getAll("<player>");

            for (Player p : players) {
                DBPlayer    db = Core.getPlayerHandler().get(p);
                if (db.getCity() != null) {
                    player.sendMessage(Text.of(db.getDisplayName() + " already belongs to a city."));
                    ret = false;
                }
            }
        }
        return ret;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                newCitizen = Core.getPlayerHandler().get(context.<Player>getOne("[player]").get());

        final Invitation        invitation = new CityInvitation(newCitizen, player, player.getCity());
        Core.getInvitationHandler().createInvitation(invitation);

        if (context.hasAny("<player>")) {
            Collection<Player>      newCitizens = context.<Player>getAll("<player>");

            for (Player newC : newCitizens) {
                DBPlayer    p = Core.getPlayerHandler().get(newC);
                Invitation inv = new CityInvitation(p, player, player.getCity());
                Core.getInvitationHandler().createInvitation(inv);
            }
        }
        return CommandResult.success();
    }
}
