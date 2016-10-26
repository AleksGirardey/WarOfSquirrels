package fr.AleksGirardey.Commands.City.Set.Permissions.Container;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import org.spongepowered.api.command.args.CommandContext;

public class PermContainerAllies extends CityCommandSetPerm {
    @Override
    protected void setName(CommandContext context) {
        this.permName.add("permission_alliesContainer");
        this.value = context.<Boolean>getOne("[value]").get();
    }
}
