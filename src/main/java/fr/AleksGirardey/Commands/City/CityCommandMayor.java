package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public abstract class CityCommandMayor extends CityCommand {
    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return super.CanDoIt(player) && (player.getCity().getOwner() == player);
    }

    protected abstract boolean          SpecialCheck(DBPlayer player, CommandContext context);
}
