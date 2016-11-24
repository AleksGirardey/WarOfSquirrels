package fr.AleksGirardey.Commands.City.Cubo;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.Chunk;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CuboCommandAdd extends CityCommandAssistant {
    @Override
    protected boolean               SpecialCheck(DBPlayer player, CommandContext context) {
        if (Core.getCuboHandler().playerExists(player)) {
            Pair<Vector3i, Vector3i> pair = Core.getCuboHandler().getPoints(player);

            if (pair.getL() != null && pair.getR() != null) {
                City city = Core.getChunkHandler().get(pair.getL().getX() / 16, pair.getL().getZ() / 16).getCity();

                if (city == player.getCity()) {
                    city = Core.getChunkHandler().get(pair.getR().getX() / 16, pair.getR().getZ() / 16).getCity();
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
        return null;
    }
}
