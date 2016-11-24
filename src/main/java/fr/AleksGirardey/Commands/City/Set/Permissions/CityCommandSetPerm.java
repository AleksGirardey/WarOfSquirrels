package fr.AleksGirardey.Commands.City.Set.Permissions;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class           CityCommandSetPerm extends CityCommandAssistant{

    protected Permission        perm;
    protected boolean[]         values = new boolean[3];

    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        values[0] = context.<Boolean>getOne("[build]").get();
        values[1] = context.<Boolean>getOne("[container]").get();
        values[2] = context.<Boolean>getOne("[switch]").get();
        return (true);
    }

    protected abstract void     setPerm(DBPlayer player, CommandContext context);

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        setPerm(player, context);

        perm.setBuild(values[0]);
        perm.setContainer(values[1]);
        perm.setSwitch_(values[2]);
        return CommandResult.success();
    }
}
