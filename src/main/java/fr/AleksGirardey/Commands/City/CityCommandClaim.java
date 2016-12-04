package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                CityCommandClaim extends CityCommandAssistant {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        ChunkHandler        chh = Core.getChunkHandler();
        int                 x, z;

        x = player.getUser().getPlayer().get().getLocation().getBlockX() / 16;
        z = player.getUser().getPlayer().get().getLocation().getBlockZ() / 16;

        if (!chh.exists(x, z)) {
            return (chh.canBePlaced(player.getCity(), x, z, false) || chh.canBePlaced(player.getCity(), x, z, true));
        }
        player.sendMessage(Text.of(TextColors.RED, "You can't claim here.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Chunk   chunk;
        int     x, z;

        x = player.getUser().getPlayer().get().getLocation().getBlockX() / 16;
        z = player.getUser().getPlayer().get().getLocation().getBlockZ() / 16;

        if (Core.getChunkHandler().canBePlaced(player.getCity(), x, z, false)) {
            chunk = new Chunk(player, false, false);
            Core.getChunkHandler().add(chunk);
            Text message = Text.of("La parcelle [" + chunk.getPosX() + ";" + chunk.getPosZ() + "] est maintenant sous le contrôle de votre ville.");
            Core.getInfoCityMap().get(player.getCity()).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
            return CommandResult.success();
        }
        chunk = new Chunk(player, false, true);
        Core.getChunkHandler().add(chunk);
        Text message = Text.of("Votre ville possède désormais un post avancé en " + chunk.toString());
        Core.getInfoCityMap().get(player.getCity()).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
        return CommandResult.success();
    }
}
