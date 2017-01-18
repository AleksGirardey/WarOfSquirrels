package fr.AleksGirardey.Commands.Faction.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.DBObject.Permission;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

public class SetNeutral extends SetDiplomacy {
    @Override
    protected void NewDiplomacy(DBPlayer player, Faction faction, Permission perm) {
        Annouce(player.getCity().getFaction(), faction, "neutral");
        Core.getFactionHandler().setNeutral(player.getCity().getFaction(), faction);
    }

    @Override
    protected boolean CanDoIt(DBPlayer player) { return super.CanDoIt(player); }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) { return super.SpecialCheck(player, context); }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }
}
