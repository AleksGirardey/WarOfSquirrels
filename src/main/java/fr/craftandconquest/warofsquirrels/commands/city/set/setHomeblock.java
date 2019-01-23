package fr.craftandconquest.warofsquirrels.commands.city.set;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.handlers.ChunkHandler;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Chunk;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.handlers.ChunkHandler;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class setHomeblock extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        ChunkHandler chunkHandler = Core.getChunkHandler();
        Chunk           chunk = null;
        int             x, z;

        x = player.getLastChunkX();
        z = player.getLastChunkZ();
        chunk = chunkHandler.get(x, z, player.getUser().getPlayer().get().getWorld());
        return (chunk != null && chunk.getCity() == player.getCity() && !chunk.isHomeblock());
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getChunkHandler().setHomeblock(player);
        return CommandResult.success();
    }
}
