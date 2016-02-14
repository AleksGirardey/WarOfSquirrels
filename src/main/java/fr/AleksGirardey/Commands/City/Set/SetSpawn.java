package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetSpawn extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Chunk chunk = new Chunk(player);
        ChunkHandler    chh = null;
        int             playerCityId = 0;

        chh = Core.getChunkHandler();
        playerCityId = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        return chh.exists(chunk.getX(), chunk.getZ())
                && chh.getCity(chunk.getX(), chunk.getZ()) == playerCityId
                && (chh.isHomeblock(chunk.getX(), chunk.getZ())
                || chh.isOutpost(chunk.getX(), chunk.getZ()));
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        int             x, y, z;

        x = player.getLocation().getBlockX();
        y = player.getLocation().getBlockY();
        z = player.getLocation().getBlockZ();

        Core.getChunkHandler().setSpawn(new Chunk(player), x, y, z);
        player.sendMessage(Text.of("Spawn set."));
        return CommandResult.success();
    }
}
