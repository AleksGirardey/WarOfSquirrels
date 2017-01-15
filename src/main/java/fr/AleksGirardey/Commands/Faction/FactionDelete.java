package fr.AleksGirardey.Commands.Faction;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class FactionDelete extends FactionCommandMayor {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return true; }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Core.getFactionHandler().delete(player.getCity().getFaction());
        return CommandResult.success();
    }
}
