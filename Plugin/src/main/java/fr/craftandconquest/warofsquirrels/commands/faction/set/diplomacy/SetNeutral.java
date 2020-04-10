package fr.craftandconquest.warofsquirrels.commands.faction.set.diplomacy;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Faction;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Permission;
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
