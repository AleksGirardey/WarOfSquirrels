package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CityCommandDelete extends CityCommandMayor {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int     id, permId, permId2, permId3;
        String  name = null;

        id = Core.getPlayerHandler().<Integer>getElement(player, GlobalPlayer.cityId);
        name = Core.getCityHandler().<String>getElement(id, GlobalCity.displayName);
        permId = Core.getCityHandler().<Integer>getElement(id, GlobalCity.permRes);
        permId2 = Core.getCityHandler().<Integer>getElement(id, GlobalCity.permOutside);
        permId3 = Core.getCityHandler().<Integer>getElement(id, GlobalCity.permAllies);
        Core.getCityHandler().delete(id);
        Core.getPermissionHandler().delete(permId);
        Core.getPermissionHandler().delete(permId2);
        Core.getPermissionHandler().delete(permId3);
        Text message = Text.of("[BREAKING NEWS] " + name + " has fallen !");
        Core.SendText(Text.of(TextColors.GOLD, message, TextColors.RESET));

        return CommandResult.success();
    }
}
