package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.CityCommandAssistant;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CityCommandClaim extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Chunk       chunk = new Chunk(player);

        if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())
                && !Core.getChunkHandler().canBePlaced(chunk.getX(), chunk.getZ(), false)) {
            player.sendMessage(Text.of("You can't claim here."));
            return false;
        }
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Chunk   chunk = new Chunk(player);
        int     x, z, id;

        x = chunk.getX();
        z = chunk.getZ();
        id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
        Core.getChunkHandler().add(x, z, id);
        player.sendMessage(Text.of("You successfully claim this chunk."));
        return CommandResult.success();
    }
}
