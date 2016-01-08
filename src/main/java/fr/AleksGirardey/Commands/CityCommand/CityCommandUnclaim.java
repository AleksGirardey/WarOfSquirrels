package fr.AleksGirardey.Commands.CityCommand;

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

import java.sql.SQLException;

public class CityCommandUnclaim implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player player = (Player) commandSource;
            int                     x, z, id;
            ChunkHandler ch = Core.getChunkHandler();
            PlayerHandler ph = Core.getPlayerHandler();
            CityHandler cih = Core.getCityHandler();

            try {
                Chunk chunk = new Chunk(player);
                x = chunk.getX();
                z = chunk.getZ();
                id = ph.getCity(player);
                if (ch.exists(x, z) && !ch.isHomeblock(x, z)) {
                    if (cih.getCityOwner(id).equals(player.getUniqueId().toString())) {
                        ch.delete(x, z);
                        player.sendMessage(
                                Text.of("Chunk unclaimed."));
                    } else
                        player.sendMessage(
                                Text.of("You don't have the permission to do that."));
                } else
                    player.sendMessage(
                            Text.of("You can't unclaim what's belongs to mother nature or your own Homeblock !"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return CommandResult.empty();
    }
}
