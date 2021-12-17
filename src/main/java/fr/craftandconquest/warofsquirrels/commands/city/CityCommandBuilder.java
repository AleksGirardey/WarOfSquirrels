package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public abstract class CityCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && player != null && player.getCity() != null;
    }
}
