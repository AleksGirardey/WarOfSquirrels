package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class            CityCommandUnclaim extends CityCommandAssistant {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        Chunk           chunk = Core.getChunkHandler().get(player.getPosX() / 16, player.getPosZ() / 16);

        return chunk != null
                && (chunk.getCity() == player.getCity())
                && !chunk.isHomeblock();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        Chunk                   chunk = Core.getChunkHandler().get(player.getPosX() / 16, player.getPosZ() / 16);

        Core.getChunkHandler().delete(chunk);
        Text message = Text.of("La parcelle " + chunk.toString() + " a été libérée de tout contrôle.");
        Core.getInfoCityMap().get(player.getCity()).getChannel().send(
                Text.of(TextColors.GREEN, message, TextColors.RESET));
        return CommandResult.success();
    }
}