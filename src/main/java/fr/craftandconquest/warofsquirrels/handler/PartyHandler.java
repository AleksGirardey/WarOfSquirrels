package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.channels.PartyChannel;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

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

    public void RemoveFromParty(Player player) throws Exception {
        Party party = this.getFromPlayer(player);

        if(!party.isPlayerInParty(player)){
            throw new Exception("Player not in party");
        }

        StringTextComponent messageToParty = new StringTextComponent(player.getDisplayName() + " a quitté le groupe.");
        StringTextComponent messageToPlayer = new StringTextComponent("Vous avez quitté votre groupe.");

        messageToParty.applyTextStyle(TextFormatting.GOLD);
        messageToPlayer.applyTextStyle(TextFormatting.GOLD);

        if(player == party.getLeader()){
            // if only the leader left, delete the party
            if(party.getPlayers().size() == 0){
                WarOfSquirrels.instance.getPartyHandler().DeleteParty(player);
                // else sets a new leader, then remove the player
            } else {
                party.setLeader(party.getPlayers().get(0));
                messageToParty.appendText(" " + party.getLeader().getDisplayName() + " est maintenant le nouveau chef du groupe");
                this.doRemove(party, player, messageToParty, messageToPlayer);
            }
        } else {
            this.doRemove(party, player, messageToParty, messageToPlayer);
        }
    }

    private void doRemove(Party party, Player player, StringTextComponent messageToParty, StringTextComponent messageToPlayer){
        party.remove(player);
        WarOfSquirrels.instance.getBroadCastHandler().RemovePlayerFromTarget(party, player);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, messageToParty, true);
        player.getPlayerEntity().sendMessage(messageToPlayer);
    }

    public void DeleteParty(Player player) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getPartyFromLeader(player);
        DeleteParty(party);
    }

    public void DeleteParty(Party party) {
        StringTextComponent message = new StringTextComponent("Votre groupe a été dissout.");
        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(party, null, message, true);
        parties.remove(party);
        WarOfSquirrels.instance.getBroadCastHandler().DeleteTarget(party);
    }

    public Party getPartyFromLeader(Player player) {
        return parties.stream()
                .filter(party -> party.getLeader() == player)
                .findFirst()
                .orElse(null);
    }

    public Party getFromPlayer(Player player) {
        return parties.stream()
                .filter(party -> (party.getPlayers().contains(player) || party.getLeader() == player))
                .findFirst()
                .orElse(null);
    }

    public void DisplayInfo(Player player) {
        Party party = getFromPlayer(player);

        player.getPlayerEntity().sendMessage(new StringTextComponent("=== Groupe[" + party.size() + "] ==="));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Chef : " + party.getLeader()));
        player.getPlayerEntity().sendMessage(new StringTextComponent("Joueur(s) : " + party.getPlayers()));
    }
}
