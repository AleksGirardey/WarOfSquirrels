package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;

public class SendNormal implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Core.getBroadcastHandler().getGlobalChannel().send(commandSource, Text.of(commandContext.getOne("[text]").get()));
        return CommandResult.success();
    }
}
