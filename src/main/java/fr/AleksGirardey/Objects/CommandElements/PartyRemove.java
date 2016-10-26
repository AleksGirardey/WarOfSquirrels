package fr.AleksGirardey.Objects.CommandElements;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
<<<<<<< HEAD
import fr.AleksGirardey.Objects.War.PartyWar;
=======
import fr.AleksGirardey.Objects.PartyWar;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
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
