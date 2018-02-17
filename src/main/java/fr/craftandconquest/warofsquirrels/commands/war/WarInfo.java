package fr.craftandconquest.commands.war;

import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import fr.craftandconquest.objects.war.War;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class                WarInfo implements CommandExecutor {
    @Override
    public CommandResult    execute(CommandSource commandSource, CommandContext commandContext) throws CommandException {
        if (!(commandSource instanceof Player))
            return CommandResult.empty();
        DBPlayer player = Core.getPlayerHandler().get((Player) commandSource);
        if (commandContext.hasAny("[city]")) {
            commandContext.<War>getOne("[city]").get().displayInfo(player);
            return CommandResult.success();
        }
        if (Core.getWarHandler().getWar(player) == null) {
            player.sendMessage(Text.of("Vous ne participer actuellement à aucune guerre."));
            return CommandResult.empty();
        }
        Core.getWarHandler().getWar(player).displayInfo(player);
        return CommandResult.success();
    }
}
