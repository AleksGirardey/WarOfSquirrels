package fr.AleksGirardey.Commands.CityCommand;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class CityCommandDelete implements CommandExecutor{
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player  player = (Player) commandSource;
            int     id, permId;
            String  name = null;

            if (Core.getPlayerHandler().isOwner(player))
            {
                id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
                name = Core.getCityHandler().<String>getElement(id, "city_displayName");
                permId = Core.getCityHandler().<Integer>getElement(id, "city_permissionId");
                Core.getCityHandler().delete(id);
                Core.getPermissionHandler().delete(permId);
                Core.Send("[BREAKING NEWS] " + name + " has fallen !");
            }
            else
                player.sendMessage(Text.of("[City] You don't have the permission."));
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
