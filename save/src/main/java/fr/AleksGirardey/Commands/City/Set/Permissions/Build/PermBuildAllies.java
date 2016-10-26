package fr.AleksGirardey.Commands.City.Set.Permissions.Build;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import org.spongepowered.api.command.args.CommandContext;

public class PermBuildAllies extends CityCommandSetPerm {
    @Override
    protected void setName(CommandContext context) {
        this.permName.add("permission_alliesBuild");
        this.value = context.<Boolean>getOne("[value]").get();
    }
}
