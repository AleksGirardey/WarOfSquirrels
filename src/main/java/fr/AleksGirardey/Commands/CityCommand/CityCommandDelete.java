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
            int     id;
            String  name = null;
            try {
                if (Core.getPlayerHandler().isOwner(player))
                {
                    id = Core.getPlayerHandler().getCity(player);
                    name = Core.getCityHandler().getDisplayName(id);
                    Core.getPlugin().getServer().getBroadcastChannel().send(
                            Text.of("[BREAKING NEWS] " + name + " has fallen !"));
                    Core.getCityHandler().delete(id);
                }
                else
                    player.sendMessage(Text.of("[City] You don't have the permission."));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
