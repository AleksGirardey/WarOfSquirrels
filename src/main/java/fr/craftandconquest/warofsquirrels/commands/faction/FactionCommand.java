package fr.craftandconquest.warofsquirrels.commands.faction;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;

public abstract class FactionCommand implements CommandExecutor {

    protected boolean           CanDoIt(DBPlayer player) { return player != null && player.getCity() != null; }

    protected abstract boolean  SpecialCheck(DBPlayer player, CommandContext context);

    protected abstract CommandResult    ExecCommand(DBPlayer player, CommandContext context);

    @Override
    public CommandResult execute(CommandSource commandSource, CommandContext commandContext) {
        if (commandSource instanceof Player) {
            DBPlayer    player = Core.getPlayerHandler().get((Player) commandSource);

            if (CanDoIt(player) && SpecialCheck(player, commandContext))
                return ExecCommand(player, commandContext);
        }
        return CommandResult.empty();
    }
}
