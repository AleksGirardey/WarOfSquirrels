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
import org.spongepowered.api.text.format.TextColors;

public class LevelUp extends Commands {
    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        City                city = context.<City>getOne(Text.of("[city]")).orElse(null);
        int                 level = context.<Integer>getOne(Text.of("[level]")).orElse(1);

        if (city != null) {
            city.setRank(level);
            player.sendMessage(Text.of(TextColors.GOLD, "La ville '" + city.getDisplayName() + "' est maintenant au rang de '" + Core.getInfoCityMap().get(city).getCityRank().getName() + "'."));
            return CommandResult.success();
        } else
            return CommandResult.empty();
    }
}
