package fr.AleksGirardey.Commands.Chat;

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

public class                ChatCity implements CommandExecutor {
    @Override
    public CommandResult    execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

        if (player.getCity() != null) {
            player.getUser().getPlayer().get().setMessageChannel(Core.getInfoCityMap().get(player.getCity()).getChannel());
            player.sendMessage(Text.builder("Channel de ville verrouillé").color(TextColors.DARK_GREEN).build());
            return CommandResult.success();
        }
        player.sendMessage(Text.builder("Impossible de verrouillé le channel.").color(TextColors.RED).build());
        return CommandResult.empty();
    }
}
