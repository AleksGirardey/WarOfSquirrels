package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;

public abstract class PartyCommandLeader extends PartyCommandBuilder {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player).getLeader() == player;
    }
}
