package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public abstract class PartyCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        if (!super.CanDoIt(player)) {
            return false;
        }
        if (player == null) {
            errorMessage = "Are you a ghost ?";
            return false;
        }
        if (WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player) == null) {
            errorMessage = "You do not belong to a party.";
            return false;
        }

        return true;
    }
}
