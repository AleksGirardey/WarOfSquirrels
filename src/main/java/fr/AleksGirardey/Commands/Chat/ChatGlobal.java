package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public class ChatGlobal implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Player player = (Player) commandSource;
        player.setMessageChannel(Core.getBroadcastHandler().getGlobalChannel());
        return CommandResult.success();
    }
}
