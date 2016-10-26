package fr.AleksGirardey.Commands.War;

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
import org.spongepowered.api.text.Text;

public class DeclareWar extends CityCommandAssistant {
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        PartyWar    party = Core.getPartyHandler().getFromPlayer(player);

        if (party == null)
            player.sendMessage(Text.of("You need a party to attack. /party create"));
        return (party != null);
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        PartyWar    party = Core.getPartyHandler().getFromPlayer(player);
        int         city = context.<Integer>getOne("[enemy]").get();

        if (Core.getWarHandler().createWar(
                Core.getPlayerHandler().<Integer>getElement(player, "player_cityId"),
                city,
                party))
            return CommandResult.success();
        return CommandResult.empty();
    }
}
