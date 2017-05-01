package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class setHomeblock extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        ChunkHandler    chunkHandler = Core.getChunkHandler();
        Chunk           chunk = null;
        int             x, z;

        x = player.getLastChunkX();
        z = player.getLastChunkZ();
        chunk = chunkHandler.get(x, z, player.getUser().getPlayer().get().getWorld());
        return (chunk != null && chunk.getCity() == player.getCity()) || player.getUser().hasPermission("op.minecraft.net");
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getChunkHandler().setHomeblock(player);
        return CommandResult.success();
    }
}
