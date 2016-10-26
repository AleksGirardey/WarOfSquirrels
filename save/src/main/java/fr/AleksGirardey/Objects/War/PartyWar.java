package fr.AleksGirardey.Objects.War;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

public class        PartyWar {
    public Player   leader;
    List<Player>    players;

    public          PartyWar(Player player) {
        leader = player;
        players = new ArrayList<>();
    }

    public void     addPlayer(Player player) {
        players.add(player);
    }

    public String   playersString() {
        String      list = "";
        int         i = 0;

        for (Player p : players) {
            list += Core.getPlayerHandler().<String>getElement(p, "player_displayName");
            if (i != players.size() - 1)
                list += ", ";
            ++i;
        }
        return list;
    }

    public int      size() { return players.size() + 1; }

    public void     remove(Player player) { players.remove(player); }

    public List<Player> toList() {
        List<Player>    list = new ArrayList<>(players);
        list.add(leader);
        return list;
    }

    public void     Send(String message) {
        leader.sendMessage(Text.of(message));
    }

    public boolean  contains(Player player) { return players.contains(player) || leader.equals(player); }

    public boolean  createCityCheck() {
        for (Player p : players)
            if (Core.getPlayerHandler().<Integer>getElement(p, "player_cityId") != null)
                return false;
        if (Core.getPlayerHandler().<Integer>getElement(leader, "player_cityId") != null)
            return false;
        return true;
    }
}
