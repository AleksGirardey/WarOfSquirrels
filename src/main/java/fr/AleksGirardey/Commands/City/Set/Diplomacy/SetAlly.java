package fr.AleksGirardey.Commands.City.Set.Diplomacy;

import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.Invitations.AllianceInvitation;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;


public class SetAlly extends SetDiplomacy {
    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return super.SpecialCheck(player, context);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        return super.ExecCommand(player, context);
    }

    @Override
    protected void NewDiplomacy(Player sender, int cityId2) {
        Core.getInvitationHandler().createInvitation(new AllianceInvitation(sender, cityId2));
    }
}
