package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import com.sun.org.apache.xpath.internal.operations.Bool;
import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collection;

public abstract class           SetDiplomacy extends CityCommandAssistant{

    protected abstract void     NewDiplomacy(DBPlayer player, City city, Permission perm);

    void              Annouce(City city1, City city2, String relation) {
        Core.Send("[Diplomacy Alert] " + city1.getDisplayName()
                + " now treat "
                + city2.getDisplayName()
                + " as " + relation + ".");
    }

    protected boolean           CanDoIt(DBPlayer player) {
        if (super.CanDoIt(player))
            return true;
        player.sendMessage(Text.of("You need to belong to a city or you are not enough influent to do diplomacy"));
        return false;
    }

    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        String                  cityName = context.<String>getOne("[city]").get();

        if (Core.getCityHandler().get(cityName) == null) {
            player.sendMessage(Text.builder("City `")
                    .append(Text.builder(cityName)
                            .style(TextStyles.ITALIC)
                            .build())
                    .append(Text.of("` doesn't exist !"))
                    .build());
            return false;
        }

        return true;
    }

    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        String                  cityName = context.<String>getOne("[city]").get();
        boolean                 build, container, _switch;
        Permission              perm = null;

        if (context.hasAny("<build>")) {
            build = context.<Boolean>getOne("<build>").get();
            container = context.<Boolean>getOne("<container>").get();
            _switch = context.<Boolean>getOne("<switch>").get();
            perm = Core.getPermissionHandler().add(build, container, _switch);
        }

        NewDiplomacy(player, Core.getCityHandler().get(cityName), perm);
        return CommandResult.success();
    }
}
