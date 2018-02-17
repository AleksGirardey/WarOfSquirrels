package fr.craftandconquest.warofsquirrels.commands.war;

import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.Attackable;
import fr.craftandconquest.warofsquirrels.objects.dbobject.DBPlayer;
import fr.craftandconquest.warofsquirrels.objects.war.PartyWar;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandAssistant;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class                    DeclareWar extends CityCommandAssistant {
    @Override
    protected boolean           SpecialCheck(DBPlayer player, CommandContext context) {
        PartyWar                party = Core.getPartyHandler().getFromPlayer(player);
        Attackable              target = context.<Attackable>getOne("[target]").get();

        if (party == null) {
            player.sendMessage(Text.of("You need a party to attack. /party create"));
            return false;
        }

        for (DBPlayer p : party.toList()) {
            if (p.getCity() != party.getLeader().getCity()
                    && (!Core.getFactionHandler().areEnemies(p.getCity().getFaction(), target.getFaction())
                    || !Core.getFactionHandler().areAllies(p.getCity().getFaction(), party.getLeader().getCity().getFaction()))) {
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
        Attackable              target = context.<Attackable>getOne("[target]").get();

        if (Core.getWarHandler().createWar(
                player.getCity(),
                target,
                party))
            return CommandResult.success();
        return CommandResult.empty();
    }
}
