package fr.AleksGirardey.Handlers;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {

    private List<PartyWar>      parties = new ArrayList<>();

    public PartyHandler() {}

    public void     addParty(PartyWar party) { parties.add(party); }

    public boolean  contains(DBPlayer player) {
        for (PartyWar party : parties)
            if (party.toList().contains(player))
                return true;
        return false;
    }

    public boolean  isLeader(DBPlayer player) {
        for (PartyWar party : parties)
            if (party.getLeader() == player)
                return true;
        return false;
    }

    public void     removeParty(DBPlayer p) {
        parties.remove(getPartyFromLeader(p));
    }

    public PartyWar getPartyFromLeader(DBPlayer player) {
        for (PartyWar party : parties)
            if (party.getLeader() == player)
                return party;
        return null;
    }

    public PartyWar    getFromPlayer(DBPlayer player) {
        for (PartyWar p : parties)
            if (p.contains(player))
                return p;
        return null;
    }

    public void     displayInfo(DBPlayer player) {
        PartyWar    party = getFromPlayer(player);

        player.sendMessage (Text.of("=== Party[" + party.size() + "] ==="));
        player.sendMessage (Text.of("Leader : " + party.getLeader().getDisplayName()));
        player.sendMessage (Text.of("Players : " + party.playersString()));
    }
}
