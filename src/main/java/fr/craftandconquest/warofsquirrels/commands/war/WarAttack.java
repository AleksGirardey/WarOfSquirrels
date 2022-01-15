package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ITerritoryExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.Influence;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.Party;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class WarAttack extends CityMayorOrAssistantCommandBuilder implements ITerritoryExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("attack")
                .then(getTerritoryRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);
        Territory territory = getTerritory(context);

        if (territory == null) {
            player.sendMessage(ChatText.Error("Territory does not exist."));
            return false;
        }

        if (WarOfSquirrels.instance.getConfig().isPeaceTime()) {
            player.sendMessage(ChatText.Error("You cannot declare war in time of peace !"));
            return false;
        }

        if (territory.getFortification().isProtected()) {
            player.sendMessage(ChatText.Error("This territory is protected for now."));
            return false;
        }

        if (party == null) {
            player.sendMessage(ChatText.Error("You need a party to attack. (/party create)"));
            return false;
        }

        City attackerCity = player.getCity();
        Territory home = WarOfSquirrels.instance.getTerritoryHandler().get(attackerCity);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(attackerCity.getFaction(), home);

        Faction attacker = attackerCity.getFaction();
        Faction defender = territory.getFaction();

        if (!WarOfSquirrels.instance.getFactionHandler().areAllies(attacker, defender)) {
            player.sendMessage(ChatText.Error("Your target is not your enemy."));
            return false;
        }

        if (influence == null || influence.getValue() < WarOfSquirrels.instance.getConfig().getAttackCost()) {
            player.sendMessage(ChatText.Error("Your city does not have enough influence to attack."));
            return false;
        }

        if (!attackerCity.canAttack()) {
            player.sendMessage(ChatText.Error("Your city cannot attack today."));
            return false;
        }

        for (FullPlayer p : party.toList()) {
            if (p.getCity() != party.getLeader().getCity()
                    && (!WarOfSquirrels.instance.getFactionHandler().areEnemies(p.getCity().getFaction(), territory.getFaction())
                    || !WarOfSquirrels.instance.getFactionHandler().areAllies(p.getCity().getFaction(), attackerCity.getFaction()))) {
                player.sendMessage(ChatText.Error("Your party member '" + p.getDisplayName() + "' can't participate to this war."));
                return false;
            }
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Territory target = getTerritory(context);
        City defenderCity = target.getFortification().getRelatedCity();

        City attackerCity = player.getCity();
        Territory home = WarOfSquirrels.instance.getTerritoryHandler().get(attackerCity);
        Influence influence = WarOfSquirrels.instance.getInfluenceHandler().get(attackerCity.getFaction(), home);
        Party party = WarOfSquirrels.instance.getPartyHandler().getFromPlayer(player);

        target.setGotAttackedToday(true);
        influence.SubInfluence(WarOfSquirrels.instance.getConfig().getAttackCost());
        attackerCity.setHasAttackedToday(true);

        if (WarOfSquirrels.instance.getWarHandler().CreateWar(attackerCity, defenderCity, target, party))
            return 0;
        return -1;
    }
}
