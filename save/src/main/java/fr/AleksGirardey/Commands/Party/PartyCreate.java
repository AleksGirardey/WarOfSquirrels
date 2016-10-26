package fr.AleksGirardey.Commands.Party;

<<<<<<< HEAD
import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.War.PartyWar;
=======
import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Commands.Commands;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.PartyWar;
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

<<<<<<< HEAD
public class PartyCreate extends Commands {
=======
public class PartyCreate extends CityCommandAssistant {
>>>>>>> 667e63346b81486f24d90c6f7f6af8fb74c2dce4
    @Override
    protected boolean SpecialCheck(Player player, CommandContext context) {
        if (!Core.getPartyHandler().contains(player))
            return true;
        player.sendMessage(Text.of("Leave your party before creating a new one"));
        return false;
    }

    @Override
    protected CommandResult ExecCommand(Player player, CommandContext context) {
        Core.getPartyHandler().addParty(new PartyWar(player));
        player.sendMessage(Text.of("Your party have been created"));
        return CommandResult.success();
    }
}
