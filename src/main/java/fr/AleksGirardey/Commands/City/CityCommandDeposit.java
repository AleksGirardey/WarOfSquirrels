package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class CityCommandDeposit extends CityCommand {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return !(player.getCity() == null || (context.<Integer>getOne("[amount]").get() <= player.getBalance()));
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        int                     amount = context.<Integer>getOne("[amount]").get();

        player.withdraw(amount);
        player.getCity().insert(amount);
        return CommandResult.success();
    }
}
