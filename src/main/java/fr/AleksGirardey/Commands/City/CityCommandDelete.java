package fr.AleksGirardey.Commands.City;

import fr.AleksGirardey.Handlers.PermissionHandler;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Database.GlobalCity;
import fr.AleksGirardey.Objects.Database.GlobalPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    CityCommandDelete extends CityCommandMayor {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        PermissionHandler       ph = Core.getPermissionHandler();
        Permission              perm;
        City                    city = player.getCity();

        perm = city.getPermRes();
        city.setPermRes(null);
        ph.delete(perm);
        perm = city.getPermAllies();
        city.setPermAllies(null);
        ph.delete(perm);
        perm = city.getPermOutside();
        city.setPermOutside(null);
        ph.delete(perm);
        Core.getChunkHandler().deleteCity(city);
        Core.getCuboHandler().deleteCity(city);
        Core.getDiplomacyHandler().delete(city);
        for (DBPlayer p : city.getCitizens()) {
            if (p.isAssistant())
                p.setAssistant(false);
            p.setCity(null);
        }
        Core.getCityHandler().delete(city);
        Text message = Text.of("[BREAKING NEWS] " + city.getDisplayName() + " has fallen !");
        Core.SendText(Text.of(TextColors.GOLD, message, TextColors.RESET));
        return CommandResult.success();
    }
}
