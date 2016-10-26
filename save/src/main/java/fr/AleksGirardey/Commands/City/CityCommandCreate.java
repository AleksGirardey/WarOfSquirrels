package fr.AleksGirardey.Commands.City;

<<<<<<< HEAD
import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.*;
import fr.AleksGirardey.Objects.Channels.CityChannel;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Cuboide.Chunk;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
=======
import fr.AleksGirardey.Handlers.ChunkHandler;
import fr.AleksGirardey.Handlers.CityHandler;
import fr.AleksGirardey.Handlers.PlayerHandler;
import fr.AleksGirardey.Objects.Chunk;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utils;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
<<<<<<< HEAD
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CityCommandCreate extends Commands {
    @Override
    protected boolean CanDoIt(Player player) {
        if (super.CanDoIt(player) && Core.getPartyHandler().isLeader(player)) {
            if (player.hasPermission("minecraft.command.op"))
                return true;
            else if (Core.getPartyHandler().getPartyFromLeader(player).size() >= 4
                    && Core.getPartyHandler().getPartyFromLeader(player).createCityCheck())
                return true;
            player.sendMessage(Text.of(TextColors.RED, "Your party must contain 3 wanderers in order to create a city", TextColors.RESET));
            return false;
        }
        player.sendMessage(Text.of(TextColors.RED, "You need to create a party to create a city", TextColors.RESET));
        return false;
=======
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CityCommandCreate extends CityCommand {
    @Override
    protected boolean CanDoIt(Player player) {
        return true;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        PlayerHandler       plh = Core.getPlayerHandler();
        ChunkHandler        chh = Core.getChunkHandler();
<<<<<<< HEAD
        Chunk chunk = new Chunk(player);
        String              name = context.<String>getOne("City name").get();
        Text                message;

        if (plh.<Integer>getElement(player, "player_cityId") == null) {
            if (!chh.exists(chunk) && Utils.NearestHomeblock(chunk) >= ConfigLoader.distanceCities) {
                if (Utils.checkCityName(name)) {
                    if (name.length() >= 3)
                        return true;
                    message = Text.of("City name must have at least 3 characters");
                }
                else
                    message = Text.of("City name contains wrong characters or is already used.");
            } else
                 message = Text.of("You can't set a new city here ! Too close from civilization");
        } else
            message = Text.of(TextColors.RED, "Leave your city first !", TextColors.RESET);
        player.sendMessage(Text.of(TextColors.RED, message, TextColors.RESET));
=======
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
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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
<<<<<<< HEAD
        chh.setSpawn(chunk, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Core.getInfoCityMap().put(id, new InfoCity(id, 0));
        Core.getInfoCityMap().get(id).setChannel(new CityChannel(id));
        for (Player p : Core.getPartyHandler().getPartyFromLeader(player).toList()) {
            plh.setElement(p, "player_cityId", id);
            Core.getInfoCityMap().get(id).getChannel().addMember(p);
        }
        Text message = Text.of("[BREAKING NEWS] " + cityName + " have been created by " + Core.getPlayerHandler().<String>getElement(player, "player_displayName"));
        Core.SendText(Text.of(TextColors.GOLD, message, TextColors.RESET));
=======
        Core.getPermissionHandler().add(id);
        chh.setSpawn(chunk, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        Core.Send("[BREAKING NEWS] "
                + cityName
                + " have been created by "
                + Core.getPlayerHandler().<String>getElement(player, "player_displayName"));
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        return CommandResult.success();
    }
}
