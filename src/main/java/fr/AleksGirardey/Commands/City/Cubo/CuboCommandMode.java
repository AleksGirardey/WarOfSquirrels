package fr.AleksGirardey.Commands.City.Cubo;

import com.flowpowered.math.vector.Vector3i;
import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Pair;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CuboCommandMode extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        if (Core.getCuboHandler().playerExists(player))
            Core.getCuboHandler().deactivateCuboMode(player);
        else
            Core.getCuboHandler().activateCuboMode(player);
        return CommandResult.success();
    }
}
