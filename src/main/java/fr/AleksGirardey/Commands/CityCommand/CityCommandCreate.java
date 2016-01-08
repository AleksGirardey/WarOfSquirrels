package fr.AleksGirardey.Commands.CityCommand;

import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
import fr.AleksGirardey.Objects.Chunk;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.sql.SQLException;

public class CityCommandCreate implements CommandExecutor{
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            int x, z, id;
            Player  player = (Player) commandSource;
            String  cityName = commandContext.<String>getOne("City name").get();

            try {
                Chunk   chunk = new Chunk(player);
                x = chunk.getX();
                z = chunk.getZ();

                if (Core.getPlayerHandler().getCity(player) != 0) {
                    player.sendMessage(Text.of("You can't create a city if you belongs to an other one !"));
                    return CommandResult.empty();
                }
                if (Core.getChunkHandler().exists(x, z)) {
                    // ADD NEAR ALGO /!\ /!\ /!\
                    player.sendMessage(Text.of("You can't claim here."));
                    return CommandResult.empty();
                }
                if (!Utils.checkCityName(cityName)) {
                    player.sendMessage(Text.of("City name contains wrong characters or is already used."));
                    return CommandResult.empty();
                }

                Core.getCityHandler().add(player, cityName);
                Core.getPlayerHandler().setCity(player, cityName);
                id = Core.getPlayerHandler().getCity(player);
                Core.getChunkHandler().add(x, z, id);
                Core.getChunkHandler().addHomeblock(x, z);
                Core.getPermissionHandler().add(id);
                Core.getChunkHandler().setSpawn(
                        chunk,
                        player.getLocation().getBlockX(),
                        player.getLocation().getBlockY(),
                        player.getLocation().getBlockZ());
            Core.getPlugin().getServer().getBroadcastChannel().send(Text.of("[BREAKING NEWS] " + cityName + " have been created by " + Core.getPlayerHandler().getDisplayName(player)));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
