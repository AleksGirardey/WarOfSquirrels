package fr.AleksGirardey.Commands.City.Set;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

public class                    setResident extends CityCommandAssistant {

    private void                set(DBPlayer resident) {
        resident.setResident(true);
        resident.setAssistant(false);
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        DBPlayer                resident = context.<DBPlayer>getOne("[citizen]").orElse(null);

        set(resident);
        if (context.hasAny("<citizen>")) {
            Collection<DBPlayer> all = context.getAll("<citizen>");

            all.forEach(this::set);
        }
        return CommandResult.success();
    }

    private boolean     check(DBPlayer player, DBPlayer newResident) {
        Text            message;

        if (newResident == null)
            message = Text.of("Aucun citoyen ne correspond.");
        else if (newResident.getCity() != player.getCity())
            message = Text.of(newResident + " n'appartient pas a votre ville.");
        else if (player.getCity().getOwner() == newResident)
            message = Text.of(newResident + " vous ne pouvez pas retrograder votre maire.");
        else
            return true;

        player.sendMessage(Text.of(TextColors.RED, newResident + " is not a valid citizen.", TextColors.RESET));
        return false;
    }

    @Override
    protected boolean SpecialCheck(DBPlayer player, CommandContext context) {
        DBPlayer            newCitizen = context.<DBPlayer>getOne("[citizen]").orElse(null);

        if (!check(player, newCitizen))
            return false;

        if (context.hasAny("<citizen>")) {
            Collection<DBPlayer>    all = context.getAll("<citizen>");

            for (DBPlayer p : all)
                if (!check(player, p))
                    return false;
        }
        return true;
    }
}
