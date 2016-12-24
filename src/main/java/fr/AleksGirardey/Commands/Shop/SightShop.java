package fr.AleksGirardey.Commands.Shop;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class SightShop extends Commands {
    @Override
    protected boolean   CanDoIt(DBPlayer player) {
        return super.CanDoIt(player) && player.getUser().hasPermission("minecraft.command.op");
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return false;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return null;
    }
}
