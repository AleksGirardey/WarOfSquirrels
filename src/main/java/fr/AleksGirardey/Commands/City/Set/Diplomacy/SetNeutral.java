package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class SetNeutral extends SetDiplomacy {
    @Override
    protected boolean CanDoIt(DBPlayer player) { return super.CanDoIt(player); }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return super.SpecialCheck(player, context); }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }

    @Override
    protected void NewDiplomacy(DBPlayer player, City city, Permission perm) {
        Annouce(player.getCity(), city, "neutral");
        Core.getCityHandler().setNeutral(player.getCity(), city);

    }
}
