package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class PartyDelete extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return Core.getPartyHandler().isLeader(player);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Core.getPartyHandler().removeParty(player);
        return CommandResult.success();
    }
}
