package fr.craftandconquest.warofsquirrels.object.war;

import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class Party implements IChannelTarget {
    @Getter
    @Setter
    private FullPlayer leader;
    @Getter
    private final List<FullPlayer> players = new ArrayList<>();

    public Party(FullPlayer leader) {
        this.leader = leader;
    }

    public void AddPlayer(FullPlayer player) {
        players.add(player);
    }

    @Override
    public String toString() {
        StringBuilder list = new StringBuilder();
        int i = 0;

        for (FullPlayer player : players) {
            list.append(player.getDisplayName());
            if (i != players.size())
                list.append(", ");
            ++i;
        }

        return list.toString();
    }

    public int size() {
        return players.size() + 1;
    }

    public void remove(FullPlayer player) {
        players.remove(player);
    }

    public List<FullPlayer> toList() {
        List<FullPlayer> players = new ArrayList<>(getPlayers());
        players.add(leader);
        return players;
    }

    public void Send(MutableComponent text) {
        leader.sendMessage(text);
    }

    public void Send(String text) {
        Send(new TextComponent(text));
    }

    public boolean contains(FullPlayer player) {
        return players.contains(player) || leader == player;
    }

    public boolean createCityCheck() {
        for (FullPlayer p : players)
            if (p.getCity() != null)
                return false;
        return leader.getCity() == null;
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.PARTY;
    }
}
