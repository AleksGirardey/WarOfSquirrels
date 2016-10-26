package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SendCity implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Player player = (Player) commandSource;

        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId")).getChannel()
                    .send(player, Text.of(commandContext.getOne("[text]").get()));
            CommandResult.success();
        }
        return CommandResult.empty();
    }
}
