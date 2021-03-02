package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;

public abstract class CityCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) && player != null && player.getCity() != null;
    }
}
