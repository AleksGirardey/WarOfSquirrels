package fr.craftandconquest.commands.utils;

import fr.craftandconquest.commands.Commands;
import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.City;
import fr.craftandconquest.objects.dbobject.DBPlayer;
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
