package fr.AleksGirardey.Commands.City;

<<<<<<< HEAD
import fr.AleksGirardey.Objects.Cuboide.Chunk;
=======
import fr.AleksGirardey.Objects.Chunk;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
<<<<<<< HEAD
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

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
<<<<<<< HEAD
        Text message = Text.of("La parcelle " + chunk.toString() + " a été libérée de tout contrôle.");
        Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId")).getChannel().send(
                Text.of(TextColors.GREEN, message, TextColors.RESET));
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        return CommandResult.success();
    }
}