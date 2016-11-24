package fr.AleksGirardey.Commands.City.Set.Permissions;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

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
        String                  cityName = context.<String>getOne("[city]").orElse("");
        List<City>              list = new ArrayList<>(Core.getDiplomacyHandler().getAllies(player.getCity()));

        list.addAll(Core.getDiplomacyHandler().getEnemies(player.getCity()));
        if (list.contains(Core.getCityHandler().get(cityName)))
            return true;
        player.sendMessage(Text.of("There is no diplomacy between your city and `" + cityName + "`"));
        return false;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        String                  cityName = context.<String>getOne("[city]").orElse("");
        City                    city = Core.getCityHandler().get(cityName);
        List<Diplomacy>         list = Core.getDiplomacyHandler().get(player.getCity());
        Permission              perm;

        perm = new Permission(
                context.<Boolean>getOne("[build]").get(),
                context.<Boolean>getOne("[container]").get(),
                context.<Boolean>getOne("[switch]").get());

        for (Diplomacy d : list) {
            if (d.getMain() == player.getCity() && d.getSub() == city)
                d.setPermissionMain(perm);
            else if (d.getMain() == city && d.getSub() == player.getCity())
                d.setPermissionSub(perm);
        }
        return CommandResult.success();
    }
}