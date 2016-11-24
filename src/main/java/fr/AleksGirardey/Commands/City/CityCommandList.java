package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

public class                CityCommandList implements CommandExecutor {
    public CommandResult    execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        List<String>        cities = Core.getCityHandler().getCityNameList();

        if (commandSource instanceof Player)
        {
            DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);

            player.sendMessage(Text.of("Cities [" + cities.size() + "]"));
            for (String str : cities) {
                player.sendMessage(Text.of(str));
            }
        }
        return CommandResult.success();
    }
}
