package fr.craftandconquest.commands.city.set.permissions;

import fr.craftandconquest.commands.city.CityCommandAssistant;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import fr.craftandconquest.objects.dbobject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.format.TextColors;

public abstract class           CityCommandSetPerm extends CityCommandAssistant{

    protected String            name;
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
        Core.getBroadcastHandler().cityChannel(player.getCity(), "Les permissions '" + name + "' sont désormais " + perm, TextColors.GOLD);
        return CommandResult.success();
    }
}
