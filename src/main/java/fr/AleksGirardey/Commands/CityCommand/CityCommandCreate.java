package fr.AleksGirardey.Commands.CityCommand;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandCreate implements CommandExecutor{
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            int x, z, id;
            Player  player = (Player) commandSource;
            String  cityName = commandContext.<String>getOne("City name").get();
            Chunk   chunk = new Chunk(player);
            x = chunk.getX();
            z = chunk.getZ();

            if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != 0) {
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
            Core.getPlayerHandler().<Integer>setElement(
                    player,
                    "player_cityId",
                    Core.getCityHandler().getCityFromName(cityName));
            id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
            Core.getChunkHandler().add(x, z, id);
            Core.getChunkHandler().addHomeblock(x, z);
            Core.getPermissionHandler().add(id);
            Core.getChunkHandler().setSpawn(
                    chunk,
                    player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ());
            Core.Send("[BREAKING NEWS] "
                    + cityName
                    + " have been created by "
                    + Core.getPlayerHandler().<String>getElement(player, "player_displayName"));
            return CommandResult.success();
        }
        return CommandResult.empty();
    }
}
