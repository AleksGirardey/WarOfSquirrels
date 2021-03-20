package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.object.Player;

public abstract class FactionCommandMayor extends FactionCommand {

    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player)
                && (player.getCity().getFaction().getCapital().getOwner().equals(player));
    }
}
