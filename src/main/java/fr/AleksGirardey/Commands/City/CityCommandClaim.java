package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class CityCommandClaim extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Chunk       chunk = new Chunk(player);
        int         id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        if (!Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())) {
            if (Core.getChunkHandler().canBePlaced(id, chunk.getX(), chunk.getZ(), false))
                return true;
            else if (Core.getChunkHandler().canBePlaced(id, chunk.getX(), chunk.getZ(), true))
                return true;
        }
        player.sendMessage(Text.of(TextColors.RED, "You can't claim here.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Chunk   chunk = new Chunk(player);
        int     x, z, id;

        x = chunk.getX();
        z = chunk.getZ();
        id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        if (Core.getChunkHandler().canBePlaced(id, x, z, false)) {
            Core.getChunkHandler().add(x, z, id);
            Text message = Text.of("La parcelle [" + chunk.getX() + ";" + chunk.getZ() + "] est maintenant sous le contrôle de votre ville.");
            Core.getInfoCityMap().get(id).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
            return CommandResult.success();
        }
        Core.getChunkHandler().addOutpost(player, x, z);
        Text message = Text.of("Votre ville possède désormais un post avancé en " + chunk.toString());
        Core.getInfoCityMap().get(id).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
        return CommandResult.success();
    }
}
