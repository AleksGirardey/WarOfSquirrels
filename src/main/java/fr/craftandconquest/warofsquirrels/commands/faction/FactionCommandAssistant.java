package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public abstract class FactionCommandAssistant extends FactionCommand {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && player.getCity().getOwner().equals(player);
    }
}
