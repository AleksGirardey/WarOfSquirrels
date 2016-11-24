package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class PartyInfo implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (!(commandSource instanceof Player))
            return CommandResult.empty();
        DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);
        if (!Core.getPartyHandler().contains(player))
            return CommandResult.empty();
        Core.getPartyHandler().displayInfo(player);
        return CommandResult.success();
    }
}
