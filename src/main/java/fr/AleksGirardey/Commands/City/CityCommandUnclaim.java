package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CityCommandUnclaim extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Chunk   chunk = new Chunk(player);

        return Core.getChunkHandler().exists(chunk)
                && (Core.getChunkHandler().getCity(chunk.getX(), chunk.getZ()) == Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"))
                && !Core.getChunkHandler().isHomeblock(chunk.getX(), chunk.getZ());
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Chunk       chunk = new Chunk(player);

        Core.getChunkHandler().delete(chunk.getX(), chunk.getZ());
        return CommandResult.success();
    }
}