package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.PartyChannel;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {
    private final List<Party> parties = new ArrayList<>();

    public PartyHandler() {}

    public void CreateParty(Player leader) {
        Party party = new Party(leader);

        AddParty(party);
        WarOfSquirrels.instance.getBroadCastHandler().AddTarget(party, new PartyChannel());
        WarOfSquirrels.instance.getBroadCastHandler().AddPlayerToTarget(party, leader);
    }

    private void AddParty(Party party) {
        parties.add(party);
    }

    public boolean Contains(Player player) {
        for (Party party : parties) {
            if (party.toList().contains(player))
                return true;
        }
        return false;
    }

    public boolean IsLeader(Player player) {
        for (Party party : parties) {
            if (party.getLeader().equals(player))
                return true;
        }
        return false;
    }

    public void RemoveParty(Player player) {
        Party party = getPartyFromLeader(player);
        RemoveParty(party);
    }

    public void RemoveParty(Party party) {
        parties.remove(party);
        WarOfSquirrels.instance.getBroadCastHandler().DeleteTarget(party);
    }

    public Party getPartyFromLeader(Player player) {
        return parties.stream().filter(party -> party.getLeader()== player).findFirst().orElse(null);
    }

    public Party getFromPlayer(Player player) {
        return parties.stream().filter(party -> party.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public void DisplayInfo(Player player) {
        Party party = getFromPlayer(player);

        player.getPlayerEntity().sendMessage(new StringTextComponent("=== Groupe[" + party.size() + "] ==="));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Chef : " + party.getLeader()));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Joueurs : " + party.toList()));
    }
}
