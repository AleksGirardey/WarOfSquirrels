package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class            SetSpawn extends CityCommandAssistant {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        Chunk           chunk = Core.getChunkHandler().get(
                player.getUser().getPlayer().get().getLocation().getBlockX() / 16,
                player.getUser().getPlayer().get().getLocation().getBlockZ() / 16);

        return (chunk.getCity() == player.getCity() && (chunk.isHomeblock() || chunk.isOutpost()));
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        int                     x, y, z;
        Chunk                   chunk;
        Player                  p = player.getUser().getPlayer().get();

        x = p.getLocation().getBlockX();
        y = p.getLocation().getBlockY();
        z = p.getLocation().getBlockZ();

        chunk = Core.getChunkHandler().get(x / 16, z / 16);
        chunk.setRespawnX(x);
        chunk.setRespawnY(y);
        chunk.setRespawnZ(z);

        player.sendMessage(Text.of("Spawn set."));

        return CommandResult.success();
    }
}
