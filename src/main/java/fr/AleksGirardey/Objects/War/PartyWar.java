package fr.AleksGirardey.Objects.War;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class                    PartyWar {
    private DBPlayer            leader;
    private List<DBPlayer>      players;

    public          PartyWar(DBPlayer player) {
        leader = player;
        players = new ArrayList<>();
    }

    public void     addPlayer(DBPlayer player) {
        players.add(player);
    }

    public String   playersString() {
        String      list = "";
        int         i = 0;

        for (DBPlayer p : players) {
            list += p.getDisplayName();
            if (i != players.size() - 1)
                list += ", ";
            ++i;
        }
        return list;
    }

    public int      size() { return players.size() + 1; }

    public void     remove(DBPlayer player) { players.remove(player); }

    public List<DBPlayer> toList() {
        List<DBPlayer>    list = new ArrayList<>(players);
        list.add(leader);
        return list;
    }

    public void     Send(String message) {
        leader.sendMessage(Text.of(message));
    }

    public boolean  contains(DBPlayer player) { return players.contains(player) || leader.equals(player); }

    public boolean  createCityCheck() {
        for (DBPlayer p : players)
            if (p.getCity() != null)
                return false;
        return leader.getCity() == null;
    }

    public DBPlayer getLeader() {
        return leader;
    }

    public void setLeader(DBPlayer leader) {
        this.leader = leader;
    }
}
