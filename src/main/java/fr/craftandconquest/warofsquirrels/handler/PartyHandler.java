package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.war.PartyWar;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;

public class PartyHandler {
    private final List<PartyWar> parties = new ArrayList<>();

    public PartyHandler() {}

    public void AddParty(PartyWar party) {
        parties.add(party);
    }

    public boolean Contains(Player player) {
        for (PartyWar party : parties) {
            if (party.toList().contains(player))
                return true;
        }
        return false;
    }

    public boolean IsLeader(Player player) {
        for (PartyWar party : parties) {
            if (party.getLeader().equals(player))
                return true;
        }
        return false;
    }

    public void RemoveParty(Player player) {
        parties.remove(getPartyFromLeader(player));
    }

    public PartyWar getPartyFromLeader(Player player) {
        return parties.stream().filter(party -> party.getLeader()== player).findFirst().orElse(null);
    }

    public PartyWar getFromPlayer(Player player) {
        return parties.stream().filter(party -> party.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public void DisplayInfo(Player player) {
        PartyWar partyWar = getFromPlayer(player);

        player.getPlayerEntity().sendMessage(new StringTextComponent("=== Party[" + partyWar.size() + "] ==="));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Leader : " + partyWar.getLeader()));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Players : " + partyWar.toList()));
    }
}
