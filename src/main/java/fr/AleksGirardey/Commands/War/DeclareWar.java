package fr.AleksGirardey.Commands.War;

import fr.AleksGirardey.Commands.City.CityCommandAssistant;
import fr.AleksGirardey.Objects.Core;
import fr.AleksGirardey.Objects.DBObject.City;
import fr.AleksGirardey.Objects.DBObject.DBPlayer;
import fr.AleksGirardey.Objects.DBObject.Faction;
import fr.AleksGirardey.Objects.Utilitaires.ConfigLoader;
import fr.AleksGirardey.Objects.War.PartyWar;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    DeclareWar extends CityCommandAssistant {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        PartyWar                party = Core.getPartyHandler().getFromPlayer(player);

        if (party == null) {
            player.sendMessage(Text.of("You need a party to attack. /party create"));
            return false;
        }

        for (DBPlayer p : party.toList()) {
            if (p.getCity() != party.getLeader().getCity()
                    && (!Core.getFactionHandler().areEnemies(p.getCity().getFaction(), context.<Faction>getOne("[enemy]").get()))
                    || !Core.getFactionHandler().areAllies(p.getCity().getFaction(), party.getLeader().getCity().getFaction())) {
                player.sendMessage(Text.of("Your party member '" + p.getDisplayName() + "' can't participate to this war."));
                return false;
            }
        }

        if (Core.getConfig().isPeaceTime())
            player.sendMessage(Text.of(TextColors.DARK_RED, "You cannot declare war in time of peace !!", TextColors.RESET));
        return (!Core.getConfig().isPeaceTime());
    }

    @Override
    protected CommandResult     ExecCommand(DBPlayer player, CommandContext context) {
        PartyWar                party = Core.getPartyHandler().getFromPlayer(player);
        City                    city = Core.getCityHandler().get(context.<Integer>getOne("[enemy]").get());

        if (Core.getWarHandler().createWar(
                player.getCity(),
                city,
                party))
            return CommandResult.success();
        return CommandResult.empty();
    }
}
