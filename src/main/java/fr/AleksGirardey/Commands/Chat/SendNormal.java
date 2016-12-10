package fr.AleksGirardey.Commands.Chat;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.Utilitaires.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class SendNormal implements CommandExecutor {
    @Override
    public CommandResult        execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);
        Collection<Player> online = Core.getPlugin().getServer().getOnlinePlayers();
        Text message;

        message = Text.builder().append(Text.of(Utils.getChatTag(player)), Text.of(" "), Text.of(commandContext.getOne("[text]").get())).build();

        for (Player pl : online) {
            DBPlayer p = Core.getPlayerHandler().get(pl);
            if (pl.getLocation().getPosition().distance(player.getUser().getPlayer().get().getLocation().getPosition()) <= ConfigLoader.sayDistance)
                p.sendMessage(message);
        }
        return CommandResult.success();
    }
}
