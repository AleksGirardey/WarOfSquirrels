package fr.AleksGirardey.Commands.City;

<<<<<<< HEAD
import fr.AleksGirardey.Objects.Cuboide.Chunk;
=======
import fr.AleksGirardey.Objects.Chunk;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
<<<<<<< HEAD
import org.spongepowered.api.text.format.TextColors;
=======
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4

public class CityCommandClaim extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        Chunk       chunk = new Chunk(player);
<<<<<<< HEAD
        int         id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");

        if (!Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())) {
            if (Core.getChunkHandler().canBePlaced(id, chunk.getX(), chunk.getZ(), false))
                return true;
            else if (Core.getChunkHandler().canBePlaced(id, chunk.getX(), chunk.getZ(), true))
                return true;
        }
        player.sendMessage(Text.of(TextColors.RED, "You can't claim here.", TextColors.RESET));
        return false;
=======

        if (Core.getChunkHandler().exists(chunk.getX(), chunk.getZ())
                && !Core.getChunkHandler().canBePlaced(chunk.getX(), chunk.getZ(), false)) {
            player.sendMessage(Text.of("You can't claim here."));
            return false;
        }
        return true;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Chunk   chunk = new Chunk(player);
        int     x, z, id;

        x = chunk.getX();
        z = chunk.getZ();
        id = Core.getPlayerHandler().<Integer>getElement(player, "player_cityId");
<<<<<<< HEAD
        if (Core.getChunkHandler().canBePlaced(id, x, z, false)) {
            Core.getChunkHandler().add(x, z, id);
            Text message = Text.of("La parcelle [" + chunk.getX() + ";" + chunk.getZ() + "] est maintenant sous le contrôle de votre ville.");
            Core.getInfoCityMap().get(id).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
            return CommandResult.success();
        }
        Core.getChunkHandler().addOutpost(player, x, z);
        Text message = Text.of("Votre ville possède désormais un post avancé en " + chunk.toString());
        Core.getInfoCityMap().get(id).getChannel().send(Text.of(TextColors.GREEN, message, TextColors.RESET));
=======
        Core.getChunkHandler().add(x, z, id);
        player.sendMessage(Text.of("You successfully claim this chunk."));
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
        return CommandResult.success();
    }
}
