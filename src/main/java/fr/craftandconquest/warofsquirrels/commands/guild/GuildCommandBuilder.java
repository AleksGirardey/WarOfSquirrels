package fr.craftandconquest.warofsquirrels.commands.guild;

import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;

public abstract class GuildCommandBuilder extends CommandBuilder {
    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) && player!= null && player.getGuild() != null;
    }
}