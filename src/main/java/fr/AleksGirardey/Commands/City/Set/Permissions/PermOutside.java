package fr.AleksGirardey.Commands.City.Set.Permissions;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public class PermOutside extends CityCommandSetPerm{
    @Override
    protected void setPerm(DBPlayer player, CommandContext context) {
        this.perm = player.getCity().getPermOutside();
    }
}
