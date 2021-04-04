package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class FactionSetNeutral extends FactionSetDiplomacy {
    @Override
    protected void NewDiplomacy(Player player, Faction faction, Permission perm) {
        Faction pFaction = player.getCity().getFaction();
        Annouce(pFaction, faction, "neutre");
        WarOfSquirrels.instance.getDiplomacyHandler().SetNeutral(pFaction, faction);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("neutral").then(getFactionRegister().executes(this));
    }
}
