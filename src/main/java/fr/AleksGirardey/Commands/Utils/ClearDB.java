package fr.AleksGirardey.Commands.Utils;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class ClearDB extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getUser().hasPermission("op.minecraft.net");
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        String              arg = context.<String>getOne("[argument]").orElse("all");

        switch (arg) {
            case "all" : Core.clear(); break;
            case "fake" : Core.getPlayerHandler().clearFake(); break;
        }
        return null;
    }
}
