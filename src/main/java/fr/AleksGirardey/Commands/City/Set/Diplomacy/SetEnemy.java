package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Diplomacy;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

import java.util.Objects;

public class SetEnemy extends SetDiplomacy {
    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return super.CanDoIt(player);
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        String          cityName = context.<String>getOne("[city]").orElse("");

        if (cityName.equals("")) {
            City city = Core.getCityHandler().get(cityName);

            for (Diplomacy d : Core.getDiplomacyHandler().get(player.getCity())) {
                if (d.getMain() == player.getCity() && d.getSub() == city)
                    return false;
            }
        }
        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }

    protected void          NewDiplomacy(DBPlayer player, City city2, Permission perm) {
        Annouce(player.getCity(), city2, "enemy");
        Core.getDiplomacyHandler().add(player.getCity(), city2, false, perm);
    }
}
