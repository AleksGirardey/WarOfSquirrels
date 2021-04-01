package fr.craftandconquest.warofsquirrels.commands.party;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;

public abstract class PartyCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && player != null && WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player) != null;
    }
}
