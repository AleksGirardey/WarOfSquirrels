package fr.AleksGirardey.Commands.Faction.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.DBObject.Permission;
import fr.AleksGirardey.Objects.Invitations.AllianceInvitation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;


public class SetAlly extends SetDiplomacy {

    @Override
    protected boolean       CanDoIt(DBPlayer player) {
        return super.CanDoIt(player);
    }

    @Override
    protected boolean       SpecialCheck(DBPlayer player, CommandContext context) {
        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        return super.ExecCommand(player, context);
    }

    @Override
    protected void NewDiplomacy(DBPlayer player, Faction faction, Permission perm) {
        Core.getInvitationHandler().createInvitation(new AllianceInvitation(player, faction, perm));
    }
}
