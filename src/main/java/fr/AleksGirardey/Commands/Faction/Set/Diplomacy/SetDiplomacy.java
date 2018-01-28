package fr.AleksGirardey.Commands.Faction.Set.Diplomacy;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.*;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;

public abstract class           SetDiplomacy extends CityCommandAssistant{

    protected abstract void     NewDiplomacy(DBPlayer player, Faction faction, Permission perm);

    void              Annouce(Faction factionA, Faction factionB, String relation) {
        Core.Send("[Diplomacy Alert] " + factionA.getDisplayName()
                + " now treat "
                + factionB.getDisplayName()
                + " as " + relation + ".");
    }

    protected boolean           CanDoIt(DBPlayer player) {
        if (super.CanDoIt(player))
            return true;
        player.sendMessage(Text.of("You need to belong to a faction or you are not enough influent to do diplomacy"));
        return false;
    }

    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        return player.getCity().getFaction() != context.<Faction>getOne(Text.of("[faction]")).orElse(player.getCity().getFaction());
    }

    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        Faction                 faction = context.<Faction>getOne(Text.of("[faction]")).get();
        Permission              perm = null;

        if (context.hasAny("<build>"))
            perm = new Permission(context.<Boolean>getOne("<build>").orElse(false),
                    context.<Boolean>getOne("<container>").orElse(false),
                    context.<Boolean>getOne("<switch>").orElse(false));

        NewDiplomacy(player, faction, perm);
        return CommandResult.success();
    }
}
