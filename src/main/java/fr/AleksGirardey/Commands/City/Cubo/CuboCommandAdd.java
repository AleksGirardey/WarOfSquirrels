package fr.AleksGirardey.Commands.City.Cubo;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CuboCommandAdd extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        if (Core.getCuboHandler().playerExists(player)) {
            Pair<Vector3i, Vector3i> pair = Core.getCuboHandler().getPoints(player);

            if (pair.getL() != null && pair.getR() != null) {
                int cityId = Core.getPlayerHandler().getElement(player, GlobalPlayer.cityId);
                int chunk;

                chunk = Core.getChunkHandler().getCity(
                        pair.getL().getX() / 16,
                        pair.getL().getZ() / 16);
                if (chunk == cityId) {
                    chunk = Core.getChunkHandler().getCity(
                            pair.getR().getX() / 16,
                            pair.getR().getZ() / 16);
                    if (chunk == cityId)
                        if (Utils.checkCuboName(context.<String>getOne("[name]").get(), cityId))
                            return true;
                }
            }
            player.sendMessage(Text.of("Selected boundaries need to be inside your city"));
        } else
            player.sendMessage(Text.of("Select boundaries first (/city cubo)"));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Core.getCuboHandler().add(player, context.<String>getOne("[name]").get());
        return null;
    }
}
