package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.CityCommandAssistant;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Main;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Invitations.CityInvitation;
import fr.AleksGirardey.Objects.Invitations.Invitation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Collection;
import java.util.UUID;

public class CityCommandAdd extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        PlayerHandler   playerHandler = Core.getPlayerHandler();

        if (playerHandler.getUuidFromName(context.<String>getOne("[player]").get()) != null)
        {
            Core.Send("SpecialCheck");
            if (context.hasAny("<player>")) {
                Collection<String> players = context.<String>getAll("<player>");

                for (String uuid : players)
                    if (playerHandler.getUuidFromName(uuid) == null)
                        return false;
            }
            return true;
        }
        return false;
    }

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
}
