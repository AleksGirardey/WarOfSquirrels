package fr.craftandconquest.commands.war;

import fr.craftandconquest.objects.Core;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class WarPeace implements CommandExecutor {
    @Override
    public CommandResult    execute(CommandSource src, CommandContext args) throws CommandException {
        Text                message;

        Core.getConfig().setPeaceTime(args.<Boolean>getOne("[peace]").get());

        if (Core.getConfig().isPeaceTime())
            message = Text.of(TextColors.GREEN, "Ho no.. Seems like peace have been declared", TextColors.RESET);
        else
            message = Text.of(TextColors.DARK_GREEN, "MOUHAHAHAH, TIME TO FIGHT !", TextColors.RESET);

        Core.SendText(message);
        return CommandResult.success();
    }
}
