package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
=======
import fr.AleksGirardey.Objects.Utils;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.List;

public class CityCommandList implements CommandExecutor {
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        List<String>        cities = Core.getCityHandler().getCityNameList();

        if (commandSource instanceof Player)
        {
            Player player = (Player) commandSource;

            player.sendMessage(Text.of("Cities [" + cities.size() + "]"));
            for (String str : cities) {
                player.sendMessage(Text.of(str));
            }
        }
        return CommandResult.success();
    }
}
