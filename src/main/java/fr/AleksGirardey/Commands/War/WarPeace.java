package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.common.text.serializer.xml.B;

import java.io.IOException;

public class WarPeace implements CommandExecutor {
    @Override
    public CommandResult    execute(CommandSource src, CommandContext args) throws CommandException {
        String              message;

        Core.Send("[DEBUG] Peace time changed for '" + args.<Boolean>getOne("[peace]").get());
        try {
            ConfigLoader.setPeaceTime(args.<Boolean>getOne("[peace]").get());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Core.Send("[DEBUG] Peace is now : " + (ConfigLoader.peaceTime ? "TRUE" : "FALSE"));

        if (ConfigLoader.peaceTime)
            message = "Ho no.. Seems like peace have been declared";
        else
            message = "MOUHAHAHAH, TIME TO FIGHT !";

        Core.Send(message);
        return CommandResult.success();
    }
}
