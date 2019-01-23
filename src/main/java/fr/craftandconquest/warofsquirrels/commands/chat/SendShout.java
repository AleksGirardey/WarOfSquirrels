package fr.craftandconquest.warofsquirrels.commands.chat;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.utils.Utils;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Collection;

public class SendShout implements CommandExecutor {
    @Override
    public CommandResult        execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        DBPlayer                player = Core.getPlayerHandler().get((Player) commandSource);
        Collection<Player>      online = Core.getPlugin().getServer().getOnlinePlayers();
        Text message;

        message = Text.builder().append(Text.of(Utils.getChatTag(player)), Text.of(" "), Text.of(commandContext.getOne("[text]").get())).build();

        for (Player pl : online) {
            DBPlayer p = Core.getPlayerHandler().get(pl);
            if (pl.getLocation().getPosition().distance(player.getUser().getPlayer().get().getLocation().getPosition()) <= Core.getConfig().getShoutDistance())
                p.sendMessage(message);
        }
        return CommandResult.success();
    }
}