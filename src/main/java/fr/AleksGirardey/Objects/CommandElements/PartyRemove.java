package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class PartyRemove extends CityCommandAssistant{
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        PartyWar        party = Core.getPartyHandler().getPartyFromLeader(player);

        party.remove(context.<Player>getOne("[player]").get());
        return CommandResult.success();
    }
}
