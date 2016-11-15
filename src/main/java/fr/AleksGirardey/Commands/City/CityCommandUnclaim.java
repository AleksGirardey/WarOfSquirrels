package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

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
        Text message = Text.of("La parcelle " + chunk.toString() + " a été libérée de tout contrôle.");
        Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId")).getChannel().send(
                Text.of(TextColors.GREEN, message, TextColors.RESET));
        return CommandResult.success();
    }
}