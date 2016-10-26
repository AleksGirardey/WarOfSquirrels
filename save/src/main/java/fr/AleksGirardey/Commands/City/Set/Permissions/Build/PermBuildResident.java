package fr.AleksGirardey.Commands.City.Set.Permissions.Build;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import org.spongepowered.api.command.args.CommandContext;

/**
 * Created by aleks on 14/02/16.
 */
public class PermBuildResident extends CityCommandSetPerm {
    @Override
    protected void setName(CommandContext context) {
        this.permName.add("permission_residentBuild");
        this.value = context.<Boolean>getOne("[value]").get();
    }
}
