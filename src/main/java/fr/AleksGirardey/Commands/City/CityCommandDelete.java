package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.CityCommandMayor;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandDelete extends CityCommandMayor {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int     id, permId;
        String  name = null;

        id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        name = Core.getCityHandler().<String>getElement(id, "city_displayName");
        permId = Core.getCityHandler().<Integer>getElement(id, "city_permissionId");
        Core.getCityHandler().delete(id);
        Core.getPermissionHandler().delete(permId);
        Core.Send("[BREAKING NEWS] " + name + " has fallen !");

        return CommandResult.success();
    }
}
