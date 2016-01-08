package fr.AleksGirardey.Commands.CityCommand;

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

public class CityCommandClaim implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (commandSource instanceof Player) {
            Player                  player = (Player) commandSource;
            int                     x, z, id;

            try {
                Chunk chunk = new Chunk(player);
                x = chunk.getX();
                z = chunk.getZ();
                id = Core.getPlayerHandler().getCity(player);
                if (Core.getChunkHandler().exists(x, z)) {
                    //ADD NEAR ALGO
                    player.sendMessage(Text.of("You can't claim here."));
                    return CommandResult.empty();
                }
                Core.getChunkHandler().add(x, z, id);
                player.sendMessage(Text.of("You successfully claim this chunk."));
                return CommandResult.success();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return CommandResult.empty();
    }
}
