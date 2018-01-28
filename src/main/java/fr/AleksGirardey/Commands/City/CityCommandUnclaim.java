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
        Chunk           chunk = Core.getChunkHandler().get(player.getPosX() / 16, player.getPosZ() / 16, player.getUser().getPlayer().get().getWorld());
        Text            message = Text.of("");
        boolean         ret = true;

        if (chunk == null) {
            message = Text.of(TextColors.RED, "Cette parcelle n'est pas sous le contrôle de votre ville.", TextColors.RESET);
            ret = false;
        } else if (chunk.getCity() != player.getCity()) {
            message = Text.of(TextColors.RED, "Cette parcelle est sous le contrôle d'une autre ville.", TextColors.RESET);
            ret = false;
        } else if (chunk.isHomeblock()) {
            message = Text.of(TextColors.RED, "Vous ne pouvez pas libéré votre homeblock.", TextColors.RESET);
            ret = false;
        }

        player.sendMessage(message);
        return ret;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        Chunk                   chunk = Core.getChunkHandler().get(player.getPosX() / 16, player.getPosZ() / 16, player.getUser().getPlayer().get().getWorld());

        Core.getChunkHandler().delete(chunk);
        Text message = Text.of("La parcelle " + chunk.toString() + " a été libérée de tout contrôle.");
        Core.getInfoCityMap().get(player.getCity()).getChannel().send(
                Text.of(TextColors.GREEN, message, TextColors.RESET));
        return CommandResult.success();
    }
}