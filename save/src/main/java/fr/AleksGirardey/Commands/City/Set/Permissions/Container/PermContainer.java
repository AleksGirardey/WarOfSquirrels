package fr.AleksGirardey.Commands.City.Set.Permissions.Container;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import org.spongepowered.api.command.args.CommandContext;

public class PermContainer extends CityCommandSetPerm {
    @Override
    protected void setName(CommandContext context) {
        this.permName.add("permission_outsideContainer");
        this.permName.add("permission_alliesContainer");
        this.permName.add("permission_residentContainer");
        this.value = context.<Boolean>getOne("[value]").get();
    }
}
