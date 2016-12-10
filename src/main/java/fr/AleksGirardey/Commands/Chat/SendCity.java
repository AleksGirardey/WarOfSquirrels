package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class                SendCity implements CommandExecutor {
    @Override
    public CommandResult    execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        DBPlayer            player = Core.getPlayerHandler().get((Player) commandSource);
        Text                text;

        if (player.getCity() != null) {
            text = Text.builder().append(
                    Text.of(Utils.getTownChatTag(player)),
                    Text.of(" "),
                    Text.of(commandContext.getOne("[text]").get())).build();
            Core.getInfoCityMap().get(player.getCity())
                    .getChannel()
                    .send(player.getUser().getPlayer().get(), text);
            CommandResult.success();
        }
        return CommandResult.empty();
    }
}
