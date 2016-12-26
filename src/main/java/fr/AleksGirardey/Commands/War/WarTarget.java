package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Database.GlobalWar;
import fr.AleksGirardey.Objects.War.War;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class WarTarget extends Commands {
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        War             war = Core.getWarHandler().getWar(player);
        return war.getPhase().equals(GlobalWar.preparationPhase) && war.getDefender().equals(player.getCity());
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getWarHandler().getWar(player).setTarget(context.<DBPlayer>getOne("[player]").orElseGet(null));
        return null;
    }
}
