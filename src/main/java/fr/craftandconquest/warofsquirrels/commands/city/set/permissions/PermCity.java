package fr.craftandconquest.commands.city.set.permissions;

import fr.craftandconquest.commands.city.CityCommandAssistant;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

/*
    ** == ARGS ==
    ** ID ALLY - INT
    ** Perm build - BOOL
    ** Perm Container - BOOL
    ** Perm Switch - BOOL
    */

public class                    PermCity extends CityCommandAssistant {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
/*        String                  cityName = context.<String>getOne("[city]").orElse("");
        List<city>              list = new ArrayList<>(Core.getDiplomacyHandler().getAllies(player.getCity()));

        list.addAll(Core.getDiplomacyHandler().getEnemies(player.getCity()));
        if (list.contains(Core.getCityHandler().get(cityName)))
            return true;
        player.sendMessage(Text.of("There is no diplomacy between your city and `" + cityName + "`"));*/
        return false;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
/*        String                  cityName = context.<String>getOne("[city]").orElse("");
        city                    city = Core.getCityHandler().get(cityName);
        List<diplomacy>         list = Core.getDiplomacyHandler().get(player.getCity());
        Permission              perm;

        perm = new Permission(
                context.<Boolean>getOne("[build]").get(),
                context.<Boolean>getOne("[container]").get(),
                context.<Boolean>getOne("[switch]").get());

        for (diplomacy d : list) {
            if (d.getMain() == player.getCity() && d.getSub() == city)
                d.setPermissionMain(perm);
            else if (d.getMain() == city && d.getSub() == player.getCity())
                d.setPermissionSub(perm);
        } */
        return CommandResult.success();
    }
}