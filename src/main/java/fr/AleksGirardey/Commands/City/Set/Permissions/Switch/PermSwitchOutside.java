package fr.AleksGirardey.Commands.City.Set.Permissions.Switch;

import fr.AleksGirardey.Commands.City.Set.Permissions.CityCommandSetPerm;
import org.spongepowered.api.command.args.CommandContext;

public class PermSwitchOutside extends CityCommandSetPerm {
    @Override
    protected void setName(CommandContext context) {
        this.permName.add("permission_outsideSwitch");
        this.value = context.<Boolean>getOne("[value]").get();
    }
}
