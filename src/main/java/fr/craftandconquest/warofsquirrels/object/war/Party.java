package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.Player;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class Party implements IChannelTarget {
    @Getter @Setter private Player leader;
    @Getter private final List<Player> players = new ArrayList<>();

    public Party(Player leader) {
        this.leader = leader;
    }

    public void AddPlayer(Player player) {
        players.add(player);
    }

    @Override
    public String toString() {
        StringBuilder list = new StringBuilder();
        int i = 0;

        for(Player player : players) {
            list.append(player.getDisplayName());
            if (i != players.size())
                list.append(", ");
            ++i;
        }

        return list.toString();
    }

    public int size() { return players.size() + 1; }

    public void remove(Player player) { players.remove(player); }

    public List<Player> toList() {
        List<Player> players = new ArrayList<>(getPlayers());
        players.add(leader);
        return players;
    }

    public void Send(TextComponent text) {
        leader.getPlayerEntity().sendMessage(text);
    }

    public void Send(String text) {
        Send(new StringTextComponent(text));
    }

    public boolean contains(Player player) { return players.contains(player) || leader == player; }

    public boolean  createCityCheck() {
        for (Player p : players)
            if (p.getCity() != null)
                return false;
        return leader.getCity() == null;
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.PARTY;
    }
}
