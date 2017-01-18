package fr.AleksGirardey.Commands.City.Set.Permissions;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.args.CommandContext;

public class PermRecruit extends CityCommandSetPerm {
    @Override
    protected void setPerm(DBPlayer player, CommandContext context) {
        this.perm = player.getCity().getPermRec();
    }
}
