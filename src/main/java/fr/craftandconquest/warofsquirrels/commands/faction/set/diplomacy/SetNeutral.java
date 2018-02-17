package fr.craftandconquest.commands.faction.set.diplomacy;

import fr.craftandconquest.objects.Core;
import fr.craftandconquest.objects.dbobject.DBPlayer;
import fr.craftandconquest.objects.dbobject.Faction;
import fr.craftandconquest.objects.dbobject.Permission;
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
