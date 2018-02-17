package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.flowpowered.math.vector.Vector3i;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Pair;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

public class CuboCommandCreate extends CityCommandAssistant {
    @Override
    protected boolean               SpecialCheck(DBPlayer player, CommandContext context) {
        if (Core.getCuboHandler().playerExists(player)) {
            World world = player.getUser().getPlayer().get().getWorld();
            Pair<Vector3i, Vector3i> pair = Core.getCuboHandler().getPoints(player);

            if (pair.getL() != null && pair.getR() != null) {
                City city = Core.getChunkHandler().get(pair.getL().getX() / 16, pair.getL().getZ() / 16, world).getCity();

                if (city == player.getCity()) {
                    city = Core.getChunkHandler().get(pair.getR().getX() / 16, pair.getR().getZ() / 16, world).getCity();
                    if (city == player.getCity())
                        if (Utils.checkCuboName(context.<String>getOne("[name]").get(), city))
                            return true;
                }
            }
            player.sendMessage(Text.of("Selected boundaries need to be inside your city"));
        } else
            player.sendMessage(Text.of("Select boundaries first (/city cubo)"));
        return false;
    }

    @Override
    protected CommandResult         ExecCommand(DBPlayer player, CommandContext context) {
        Core.getCuboHandler().add(player, context.<String>getOne("[name]").get());
        return CommandResult.success();
    }
}
