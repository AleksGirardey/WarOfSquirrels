package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ChatCity implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        Player player = (Player) commandSource;
        if (Core.getPlayerHandler().<Integer>getElement(player, "player_cityId") != null) {
            player.setMessageChannel(Core.getInfoCityMap().get(Core.getPlayerHandler().<Integer>getElement(player, "player_cityId")).getChannel());
            player.sendMessage(Text.of(TextColors.DARK_GREEN, "Channel de ville verrouillé.", TextColors.RESET));
            return CommandResult.success();
        }
        player.sendMessage(Text.of(TextColors.RED, "Impossible de lock le channel.", TextColors.RESET));
        return CommandResult.empty();
    }
}
