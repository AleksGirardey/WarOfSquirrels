package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Channels.GlobalChannel;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.War.PartyWar;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class BroadcastHandler {

    private GlobalChannel   global;

    public BroadcastHandler() {
        global = new GlobalChannel();
    }

    public void     cityInvitationSend(Player player, Player sender, int cityId) {
        player.sendMessage(Text.of(Core.getPlayerHandler().<String>getElement(sender, "player_displayName") + " invite you to join "
                + Core.getCityHandler().<String>getElement(cityId, "city_displayName")));
        cityChannel(cityId, Core.getPlayerHandler().<String>getElement(player, "player_displayName") + " a été invité à rejoindre votre ville");
    }

    public GlobalChannel        getGlobalChannel() { return global; }

    public void cityChannel(int cityId, String message) {
        String[][]      citizens = Core.getCityHandler().getCitizens(cityId);
        List<String> uuids = new ArrayList<String>();

        for (String[] uuid : citizens)
            uuids.add(uuid[0]);
        Core.getPlugin().getServer().getOnlinePlayers().stream().filter(player -> uuids.contains(player.getUniqueId().toString())).forEach(player -> player.sendMessage(Text.of(message)));
    }

    public void         allianceInvitationSend(int citySender, int cityId) {
        List<String>    assistants = Core.getCityHandler().getAssistants(cityId);
        String          mayor = Core.getCityHandler().<String>getElement(cityId, "city_playerOwner");
        List<Player>    targets = new ArrayList<Player>();

        for (Player player : Core.getPlugin().getServer().getOnlinePlayers())
            if (assistants.contains(player.getUniqueId().toString())
                    || mayor.equals(player.getUniqueId().toString()))
                targets.add(player);

        for (Player p : targets) {
            p.sendMessage(Text.of("City " + Core.getCityHandler().<String>getElement(citySender, "city_displayName") + " want to be your ally. Use /accept or /refuse to send a reply."));
        }
    }

    public void         warAnnounce(War war, War.WarState    state) {
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
        receiver.sendMessage(Text.of(Core.getPlayerHandler().<String>getElement(sender, "player_displayName") + " invite you to join his party. Type /accept or /refuse to respond."));
    }
}