package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Channels.GlobalChannel;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BroadcastHandler {

    private GlobalChannel   global;

    public BroadcastHandler() {
        global = new GlobalChannel();
    }

    public void     cityInvitationSend(Player player, Player sender, City city) {
        DBPlayer    p1 = Core.getPlayerHandler().get(player),
                s1 = Core.getPlayerHandler().get(sender);

        player.sendMessage(Text.of(s1.getDisplayName() + " invited you to join " + city.getDisplayName()));
        cityChannel(city, p1.getDisplayName() + " has been invited to join the city.");
    }

    public GlobalChannel        getGlobalChannel() { return global; }

    public void                 cityChannel(City city, String message) {
        Collection<DBPlayer>    players = city.getCitizens();

        for (DBPlayer player : players)
            if (player.getUser().isOnline())
                player.getUser().getPlayer().get().sendMessage(Text.of(message));
    }

    public void                 allianceInvitationSend(City sender, City receiver) {
        DBPlayer                mayor = receiver.getOwner();
        Collection<DBPlayer>    assistants = receiver.getAssistants();
        String                  message;

        message = "The city " + sender.getDisplayName() + " want to be your ally. Use /accept or /refuse";
        if (mayor.getUser().isOnline())
            mayor.getUser().getPlayer().get().sendMessage(Text.of(message));
    }

    public void         warAnnounce(War war, War.WarState state) {
        String          message = "";
        if (state == War.WarState.Preparation)
            message = war.getAttackerName() + " attacks " + war.getDefenderName() + " prepare yourself for the fight. You have 2 minutes before the hostilities starts !";
        else if (state == War.WarState.War)
            message = "Let the war... BEGIN !";
        else
            message = "Care, you have 1 min to evacuate the city before the place got rollbacked";

        warAnnounce(war, message);
    }

    public void         warAnnounce(War war, String message) {
        for (Player p : war.getProtagonists())
            p.sendMessage(Text.of(message));
    }

    public void         partyChannel(PartyWar party, String message) {
        for (Player p : party.toList())
            p.sendMessage(Text.of(message));
    }

    public void         partyInvitation(Player sender, Player receiver) {
        DBPlayer        s = Core.getPlayerHandler().get(sender),
                r = Core.getPlayerHandler().get(receiver);

        receiver.sendMessage(Text.of(
                s.getDisplayName() + " invited you to join his party. Type /accept or /refuse to respond."
        ));
    }
}