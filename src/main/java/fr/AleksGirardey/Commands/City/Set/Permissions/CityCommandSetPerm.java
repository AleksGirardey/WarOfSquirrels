package fr.AleksGirardey.Commands.City.Set.Permissions;

import fr.AleksGirardey.Commands.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CityCommandSetPerm extends CityCommandAssistant{

    protected List<String>  permName = new ArrayList<String>();
    protected boolean       value;

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Boolean value = context.<Boolean>getOne("[value]").get();
        return (value != null);
    }

    protected abstract void setName(CommandContext context);

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int     permId = Core.getCityHandler().getElement(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                "city_permissionId");
        setName(context);
        for (String perm : permName) {
            Core.getPermissionHandler().setPerm(permId, perm, value);
        }
        return CommandResult.success();
    }
}
