package fr.AleksGirardey.Commands.Faction.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class SetEnemy extends SetDiplomacy {
    @Override
    protected void NewDiplomacy(DBPlayer player, Faction faction, Permission perm) {
        Annouce(player.getCity().getFaction(), faction, "enemy");
        Core.getDiplomacyHandler().add(player.getCity().getFaction(), faction, false, perm);
    }

    @Override
    protected boolean CanDoIt(DBPlayer player) {
        return super.CanDoIt(player);
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        Faction         faction = context.<Faction>getOne("[faction]").orElse(null);

        if (faction != null)
            for (Diplomacy d : Core.getDiplomacyHandler().get(player.getCity().getFaction()))
                if (d.getTarget() == faction)
                    return false;

        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }
}
