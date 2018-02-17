package fr.craftandconquest.warofsquirrels.commands.city;

import fr.craftandconquest.warofsquirrels.handlers.ChunkHandler;
import fr.craftandconquest.warofsquirrels.objects.city.CityRank;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.handlers.ChunkHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class                CityCommandClaim extends CityCommandAssistant {
    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        ChunkHandler chh = Core.getChunkHandler();
        World               world = player.getUser().getPlayer().get().getWorld();
        int                 x, z;

        x = player.getUser().getPlayer().get().getLocation().getBlockX() / 16;
        z = player.getUser().getPlayer().get().getLocation().getBlockZ() / 16;

        if (!chh.exists(x, z, world)) {
            if (chh.canBePlaced(player.getCity(), x, z, false, world)) {
                CityRank r = Core.getInfoCityMap().get(player.getCity()).getCityRank();

                if (r.getChunkMax() == chh.getSize(player.getCity())) {
                    player.sendMessage(Text.of(TextColors.RED, "Vous avez atteint la limite de chunks et ne pouvez agrandir plus votre ville.", TextColors.RESET));
                    return false;
                }
                return true;
            } else if (chh.canBePlaced(player.getCity(), x, z, true, world))
                return true;
            Core.getLogger().warn("CHUNK CAN'T BE PLACED HERE");
        }
        player.sendMessage(Text.of(TextColors.RED, "You can't claim here.", TextColors.RESET));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Player  p = player.getUser().getPlayer().get();
        World   world = p.getWorld();
        Chunk   chunk;
        int     x, z;

        x = p.getLocation().getBlockX() / 16;
        z = p.getLocation().getBlockZ() / 16;

        if (Core.getChunkHandler().canBePlaced(player.getCity(), x, z, false, world)) {
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