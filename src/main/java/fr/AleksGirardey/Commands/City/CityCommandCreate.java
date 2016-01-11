package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Commands.CityCommand;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CityCommandCreate extends CityCommand {
    @Override
    protected boolean CanDoIt(Player player) {
        return true;
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        PlayerHandler       plh = Core.getPlayerHandler();
        ChunkHandler        chh = Core.getChunkHandler();
        Chunk               chunk = new Chunk(player);
        String              name = context.<String>getOne("City name").get();

        if (plh.<Integer>getElement(player, "player_cityId") == null) {
            if (!chh.exists(chunk)) {
                if (Utils.checkCityName(name))
                    return true;
                else
                    player.sendMessage(Text.of("City name contains wrong characters or is already used."));
            } else
                player.sendMessage(Text.of("You can't set a new city here ! Too close from civilization"));
        } else
            player.sendMessage(Text.of("Leave your city first !"));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        String          cityName = context.<String>getOne("City name").get();
        CityHandler     cih = Core.getCityHandler();
        ChunkHandler    chh = Core.getChunkHandler();
        PlayerHandler   plh = Core.getPlayerHandler();
        Chunk           chunk = new Chunk(player);
        Location<World> location = player.getLocation();
        int             id = 0;

        cih.add(player, cityName);
        id = cih.getCityFromName(cityName);
        plh.<Integer>setElement(player, "player_cityId", id);
        chh.add(chunk.getX(), chunk.getZ(), id);
        chh.addHomeblock(chunk.getX(), chunk.getZ());
        Core.getPermissionHandler().add(id);
        chh.setSpawn(chunk, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Core.Send("[BREAKING NEWS] "
                + cityName
                + " have been created by "
                + Core.getPlayerHandler().<String>getElement(player, "player_displayName"));
        return CommandResult.success();
    }
}
