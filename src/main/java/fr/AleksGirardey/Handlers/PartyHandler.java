package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.PartyWar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {

    private List<PartyWar>      parties;

    public PartyHandler() { parties = new ArrayList<>(); }

    public void     addParty(PartyWar party) { parties.add(party); }

    public boolean  contains(Player player) {
        for (PartyWar party : parties)
            if (party.toList().contains(player))
                return true;
        return false;
    }

    public boolean  isLeader(Player player) {
        for (PartyWar party : parties)
            if (party.leader == player)
                return true;
        return false;
    }

    public void     removeParty(Player p) {
        parties.remove(getPartyFromLeader(p));
    }

    public PartyWar getPartyFromLeader(Player player) {
        for (PartyWar party : parties)
            if (party.leader == player)
                return party;
        return null;
    }

    public PartyWar    getFromPlayer(Player player) {
        for (PartyWar p : parties)
            if (p.contains(player))
                return p;
        return null;
    }

    public void     displayInfo(Player player) {
        PartyWar    party = getFromPlayer(player);

        player.sendMessage (Text.of("=== Party[" + party.size() + "] ==="));
        player.sendMessage (Text.of("Leader : " + Core.getPlayerHandler().<String>getElement(party.leader, "player_displayName")));
        player.sendMessage (Text.of("Players : " + party.playersString()));
    }
}
