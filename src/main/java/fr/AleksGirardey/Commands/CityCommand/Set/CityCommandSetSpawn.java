package fr.AleksGirardey.Commands.CityCommand.Set;

import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
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
import org.spongepowered.api.world.Location;

import java.sql.SQLException;

public class CityCommandSetSpawn implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            int x = player.getLocation().getBlockX(),
                y = player.getLocation().getBlockY(),
                z = player.getLocation().getBlockZ();
            CityHandler     cih = Core.getCityHandler();
            ChunkHandler    chh = Core.getChunkHandler();
            PlayerHandler   plh = Core.getPlayerHandler();
            int             cityPlayer, cityChunk;
            Location        pL = player.getLocation();

            try {
                Chunk chunk = new Chunk(player);

                if (chh.exists(chunk.getX(), chunk.getZ()))
                    cityChunk = chh.getCity(chunk.getX(), chunk.getZ());
                else {
                    player.sendMessage(Text.of("This chunk belongs to mother nature."));
                    return CommandResult.empty();
                }
                cityPlayer = plh.getCity(player);
                if (cityPlayer != 0 && cityPlayer == cityChunk) {
                    if (cih.getCityOwner(cityChunk).equals(player.getUniqueId().toString())) {
                        if (chh.setSpawn(new Chunk(player), x, y, z))
                            player.sendMessage(Text.of("Spawn set."));
                        else
                            player.sendMessage(Text.of("You can't set your spawn there, you need to go on your Homeblock or on an outpost."));
                    } else
                        player.sendMessage(Text.of("You don't have the permission to do that."));
                } else {
                    player.sendMessage(Text.of("This is not in your city"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return CommandResult.empty();
    }
}
