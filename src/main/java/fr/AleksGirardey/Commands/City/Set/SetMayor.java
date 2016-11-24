package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandMayor;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class SetMayor extends CityCommandMayor {

    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        return Core.getPlayerHandler().get(context.<String>getOne("[resident]").get()).getCity() == player.getCity();
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        City                    city = player.getCity();
        DBPlayer                newMayor = Core.getPlayerHandler().get(context.<String>getOne("[resident]").get());

        city.setOwner(newMayor);
        return CommandResult.success();
    }
}
