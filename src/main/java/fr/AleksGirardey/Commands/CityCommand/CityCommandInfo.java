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
            String  args = commandContext.<String>getOne("[city]").orElse(""),
                    sql;
            int     argument = 0;
            Player player = (Player) commandSource;
            Connection c = null;
            PreparedStatement statement = null;
            ResultSet rs = null;

            if (args.equals("")) {
                try {
                    argument = Core.getPlayerHandler().getCity(player);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (argument == 0) {
                    player.sendMessage(Text.of("You don't belong to a city bro !"));
                    return CommandResult.empty();
                }
            } else {
                try {
                    argument = Core.getCityHandler().getCityFromName(args);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (argument == 0) {
                    player.sendMessage(Text.of("City '" + args + "' does not exist."));
                    return CommandResult.empty();
                }
            }
            try {
                sql = "SELECT * FROM `City` WHERE `city_id` = ?;";
                c = Core.getDatabaseHandler().getConnection();
                statement = c.prepareStatement(sql);
                statement.setInt(1, argument);
                rs = statement.executeQuery();
                if (rs.next()) {
                    player.sendMessage(Text.of("---===| " + rs.getString("city_displayName") + "[" + Core.getCityHandler().getCitizens(rs.getInt("city_id")).length + "] |===---"));
                    player.sendMessage(Text.of("Mayor: " + Core.getPlayerHandler().get(rs.getString("city_playerOwner"), PlayerHandler.sql_tables.get(PlayerHandler.sql_values.NAME))));
                    player.sendMessage(Text.of("Citizens: " + Utils.getListFromTableString(Core.getCityHandler().getCitizens(rs.getInt("city_id")), 1)));
                    player.sendMessage(Text.of("Tag: " + rs.getString("city_tag")));
                }
                statement.close();
                c.close();
                rs.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
