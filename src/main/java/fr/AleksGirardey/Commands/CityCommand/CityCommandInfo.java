package fr.AleksGirardey.Commands.CityCommand;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CityCommandInfo implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player)
        {
            String  args = commandContext.<String>getOne("[city]").orElse("");
            int     argument = 0;
            Player player = (Player) commandSource;

            try {
            if (args.equals("")) {
                argument = Core.getPlayerHandler().getCity(player);
                if (argument == 0) {
                    player.sendMessage(Text.of("You don't belong to a city bro !"));
                    return CommandResult.empty();
                }
            } else {
                argument = Core.getCityHandler().getCityFromName(args);
                if (argument == 0) {
                    player.sendMessage(Text.of("City '" + args + "' does not exist."));
                    return CommandResult.empty();
                }
            }
                player.sendMessage(Text.of("---===| " + Core.getCityHandler().getElement(argument, "city_displayName")
                        + "[" + Core.getCityHandler().getCitizens(argument).length + "] |===---"));
                player.sendMessage(Text.of("Mayor: " + Core.getPlayerHandler().get(
                        Core.getCityHandler().<String>getElement(argument, "city_playerOwner"),
                "player_displayName")));
                player.sendMessage(Text.of("Citizens: " + Utils.getListFromTableString(Core.getCityHandler().getCitizens(argument), 1)));
                player.sendMessage(Text.of("Tag: " + Core.getCityHandler().getElement(
                        argument,
                        "city_tag")));
                return CommandResult.success();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return CommandResult.empty();
    }
}
