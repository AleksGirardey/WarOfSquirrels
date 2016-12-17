package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class CityCommandWithdraw extends CityCommand {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getCity() != null && (player.getCity().getBalance() - context.<Integer>getOne("[amount]").get()) >= 0;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        int                     amount = context.<Integer>getOne("[amount]").get();

        player.insert(amount);
        player.getCity().withdraw(amount);
        return CommandResult.success();
    }
}
