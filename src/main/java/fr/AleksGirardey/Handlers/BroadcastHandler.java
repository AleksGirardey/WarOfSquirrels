package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Channels.GlobalChannel;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BroadcastHandler {

    private GlobalChannel   global;

    public BroadcastHandler() {
        global = new GlobalChannel();
    }

    public void     cityInvitationSend(DBPlayer player, DBPlayer sender, City city) {
        player.sendMessage(Text.of(sender.getDisplayName() + " invited you to join " + city.getDisplayName() + ". Use /accept or /refuse to respond."));
        cityChannel(city, player.getDisplayName() + " has been invited to join the city.");
    }

    public GlobalChannel        getGlobalChannel() { return global; }

    public void                 cityChannel(City city, String message) {
        Collection<DBPlayer>    players = city.getCitizens();

        for (DBPlayer player : players)
            if (player.getUser().isOnline())
                player.getUser().getPlayer().get().sendMessage(Text.of(message));
    }

    public void                 factionChannel(Faction faction, String message) {
        Collection<City>        cities = faction.getCities().values();

        cities.forEach(c -> cityChannel(c, message));
    }

    public void                 allianceInvitationSend(Faction sender, Faction receiver) {
        DBPlayer                mayor = receiver.getCapital().getOwner();
        Collection<DBPlayer>    assistants = receiver.getCapital().getAssistants();
        String                  message;

        factionChannel(sender, receiver.getDisplayName() + " has been invited to be your ally.");
        message = "The city " + sender.getDisplayName() + " want to be your ally. Use /accept or /refuse";
        if (mayor.getUser().isOnline())
            mayor.getUser().getPlayer().get().sendMessage(Text.of(message));
        else {
            for (DBPlayer p : assistants)
                if (p.getUser().isOnline())
                    p.getUser().getPlayer().get().sendMessage(Text.of(message));
        }
    }

    public void         warAnnounce(War war, War.WarState state) {
        String          message = "";
        if (state == War.WarState.Preparation)
            message = war.getAttacker().getDisplayName() + " attacks " + war.getDefender().getDisplayName() + " prepare yourself for the fight. You have 2 minutes before the hostilities starts !";
        else if (state == War.WarState.War)
            message = "Let the war... BEGIN !";
        else
            message = "Care, you have 1 min to evacuate the city before the place got rollbacked";

        warAnnounce(war, message);
    }

    public void         warAnnounce(War war, String message) {
        for (DBPlayer p : war.getProtagonists())
            p.sendMessage(Text.of(message));
    }

    public void         partyChannel(PartyWar party, String message) {
        for (DBPlayer p : party.toList())
            p.sendMessage(Text.of(TextColors.YELLOW, message, TextColors.RESET));
    }

    public void         partyInvitation(DBPlayer sender, DBPlayer receiver) {
        partyChannel(Core.getPartyHandler().getFromPlayer(sender), receiver.getDisplayName() + " has been invited to your party.");
        receiver.sendMessage(Text.of(TextColors.YELLOW,
                sender.getDisplayName() + " invited you to join his party. Type /accept or /refuse to respond.",
                TextColors.RESET
        ));
    }
}