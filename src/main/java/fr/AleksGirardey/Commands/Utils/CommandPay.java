package fr.AleksGirardey.Commands.Utils;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class CommandPay extends Commands{
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return (player.getBalance() - context.<Integer>getOne("[amount]").get()) >= 0;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        int                     amount = context.<Integer>getOne("[amount]").get();
        DBPlayer                pl = Core.getPlayerHandler().get(context.<Player>getOne("[player]").get());

        player.withdraw(amount);
        pl.insert(amount);
        return CommandResult.success();
    }
}
