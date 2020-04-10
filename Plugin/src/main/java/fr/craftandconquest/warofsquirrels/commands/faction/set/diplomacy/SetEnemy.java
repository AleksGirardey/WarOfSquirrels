package fr.craftandconquest.warofsquirrels.commands.faction.set.diplomacy;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;

import java.util.List;

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
        List<Diplomacy> diplomacies = Core.getDiplomacyHandler().get(player.getCity().getFaction());

        if (faction != null && diplomacies != null) {
            for (Diplomacy d : diplomacies)
                if (d.getTarget() == faction)
                    return false;
        }

        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }
}
