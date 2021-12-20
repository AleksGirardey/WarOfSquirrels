package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public abstract class PartyCommandLeader extends PartyCommandBuilder {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player).getLeader() == player;
    }
}
