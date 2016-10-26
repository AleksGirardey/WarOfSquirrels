package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Invitations.CityInvitation;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.registry.util.OverrideRegistration;
import org.spongepowered.api.text.Text;

import java.util.Collection;
import java.util.UUID;

public class CityCommandAdd extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Player      newCitizen = context.<Player>getOne("[player]").get();
        boolean     ret = true;

        if (Core.getPlayerHandler().<Integer>getElement(newCitizen, "player_cityId") != null) {
            player.sendMessage(Text.of(Core.getPlayerHandler().<String>getElement(player, "player_displayName") + " is already in a city."));
            ret = false;
        }
        if (context.hasAny("<player>")) {
            Collection<Player>  players = context.<Player>getAll("<player>");

            for (Player p : players) {
                if (Core.getPlayerHandler().<Integer>getElement(p, "player_cityId") != null) {
                    player.sendMessage(Text.of(Core.getPlayerHandler().<String>getElement(p, "player_displayName") + " is already in a city."));
                    ret = false;
                }
            }
        }
        return ret;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Player      newCitizen = context.<Player>getOne("[player]").get();
        int         cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        final Invitation invitation = new CityInvitation(newCitizen, player, cityId);
        Core.getInvitationHandler().createInvitation(invitation);

        if (context.hasAny("<player>")) {
            Collection<Player>      newCitizens = context.<Player>getAll("<player>");

            for (Player newC : newCitizens) {
                Invitation inv = new CityInvitation(newC, player, cityId);
                Core.getInvitationHandler().createInvitation(inv);
            }
        }
        return CommandResult.success();
    }

    /*
    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Collection<Player>  online = Core.getPlugin().getServer().getOnlinePlayers();
        PlayerHandler       playerHandler = Core.getPlayerHandler();
        String              playerUUID;
        Player              tmp;
        int                 cityId;

        playerUUID = playerHandler.getUuidFromName(context.<String>getOne("[player]").get());
        tmp = Core.getPlugin().getServer().getPlayer(UUID.fromString(playerUUID)).orElse(null);
        cityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        if (tmp != null && online.contains(tmp))
        {
            final Invitation invitation = new CityInvitation(tmp, player, cityId);
            Core.getInvitationHandler().createInvitation(invitation);
            if (context.hasAny("<player>")){
                Collection<String>  players = context.<String>getAll("<player>");

                for(String pl : players)
                {
                    playerUUID = playerHandler.getUuidFromName(pl);
                    tmp = Core.getPlugin().getServer().getPlayer(UUID.fromString(playerUUID)).orElse(null);
                    if (tmp != null && online.contains(tmp))
                        Core.getInvitationHandler().createInvitation(new CityInvitation(tmp, player, cityId));
                }
            }
        }

        return CommandResult.empty();
    }
    */
}
