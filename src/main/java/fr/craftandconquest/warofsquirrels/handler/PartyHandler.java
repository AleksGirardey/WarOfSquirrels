package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.channels.PartyChannel;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {
    private final List<Party> parties = new ArrayList<>();

    public PartyHandler() {
    }

    public void CreateParty(FullPlayer leader) {
        Party party = new Party(leader);

        AddParty(party);
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(party, new PartyChannel());
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(party, leader);
    }

    private void AddParty(Party party) {
        parties.add(party);
    }

    public boolean Contains(FullPlayer player) {
        for (Party party : parties) {
            if (party.toList().contains(player))
                return true;
        }
        return false;
    }

    public boolean IsLeader(FullPlayer player) {
        for (Party party : parties) {
            if (party.getLeader().equals(player))
                return true;
        }
        return false;
    }

    public void RemoveParty(FullPlayer player) {
        Party party = getPartyFromLeader(player);
        RemoveParty(party);
    }

    public void RemoveParty(Party party) {
        parties.remove(party);
        WarOfSquirrels.instance.getBroadCastHandler().DeleteTarget(party);
    }

    public Party getPartyFromLeader(FullPlayer player) {
        return parties.stream()
                .filter(party -> party.getLeader() == player)
                .findFirst()
                .orElse(null);
    }

    public Party getFromPlayer(FullPlayer player) {
        return parties.stream()
                .filter(party -> (party.getPlayers().contains(player) || party.getLeader() == player))
                .findFirst()
                .orElse(null);
    }

    public void DisplayInfo(FullPlayer player) {
        Party party = getFromPlayer(player);

        player.sendMessage(ChatText.Colored("=== Groupe[" + party.size() + "] ===", ChatFormatting.BLUE));
        player.sendMessage(ChatText.Colored("Chef : " + party.getLeader(), ChatFormatting.BLUE));
        player.sendMessage(ChatText.Colored("Joueur(s) : " + party.getPlayers(), ChatFormatting.BLUE));
    }
}
