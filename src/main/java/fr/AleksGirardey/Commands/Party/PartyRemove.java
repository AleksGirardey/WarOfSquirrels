package fr.AleksGirardey.Commands.Party;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;

public class            PartyRemove extends CityCommandAssistant{
    @Override
    protected boolean   SpecialCheck(DBPlayer player, CommandContext context) {
        return true;
    }

    @Override
    protected CommandResult ExecCommand(DBPlayer player, CommandContext context) {
        PartyWar        party = Core.getPartyHandler().getPartyFromLeader(player);

        party.remove(Core.getPlayerHandler().get(context.<Player>getOne("[player]").get()));
        return CommandResult.success();
    }
}
