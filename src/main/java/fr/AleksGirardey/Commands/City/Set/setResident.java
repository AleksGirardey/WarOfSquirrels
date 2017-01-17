package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    setResident extends CityCommandAssistant {
    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                resident = context.<DBPlayer>getOne("[citizen]").orElse(null);

        resident.setResident(true);
        resident.setAssistant(false);
        return CommandResult.success();
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer            newCitizen = context.<DBPlayer>getOne("[citizen]").orElse(null);

        if (newCitizen == null) {
            Core.getLogger().info("[DEBUG] NOT NULL");
            return false;
        } else if (newCitizen.getCity() != player.getCity()) {
            Core.getLogger().info("[DEBUG] Not in the city");
            return false;
        } else if (player.getCity().getOwner() == newCitizen) {
            Core.getLogger().info("[DEBUG] Mayor : " + player.getCity().getOwner().getDisplayName() + " !!!");
            return false;
        } else
            return true;

//        player.sendMessage(Text.of(TextColors.RED, newCitizen + " is not a valid citizen.", TextColors.RESET));
//        return false;
    }
}
