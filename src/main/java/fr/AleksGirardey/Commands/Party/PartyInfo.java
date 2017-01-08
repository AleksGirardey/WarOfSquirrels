package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class PartyInfo implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (!(commandSource instanceof Player))
            return CommandResult.empty();
        DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);
        if (!Core.getPartyHandler().contains(player)) {
            player.sendMessage(Text.of(TextColors.RED, "You don't belong to a party", TextColors.RESET));
            return CommandResult.empty();
        }
        Core.getPartyHandler().displayInfo(player);
        return CommandResult.success();
    }
}
