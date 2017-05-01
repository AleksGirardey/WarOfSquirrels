package fr.AleksGirardey.Commands.Utils;

import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.City.CityRank;
import fr.AleksGirardey.Objects.City.InfoCity;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public class LevelUp extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        City                city = Core.getCityHandler().get(context.<String>getOne(Text.of("[city]")).orElse(""));
        int                 level = context.<Integer>getOne(Text.of("[level]")).orElse(1);

        city.setRank(level);
        return CommandResult.success();
    }
}
