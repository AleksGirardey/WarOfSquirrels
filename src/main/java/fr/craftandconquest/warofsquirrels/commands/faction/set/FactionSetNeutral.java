package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class FactionSetNeutral extends FactionSetDiplomacy {
    @Override
    protected void NewDiplomacy(FullPlayer player, Faction faction, Permission perm) {
        Faction pFaction = player.getCity().getFaction();
        Annouce(pFaction, faction, "neutre");
        WarOfSquirrels.instance.getDiplomacyHandler().SetNeutral(pFaction, faction);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("neutral").then(getFactionRegister().executes(this));
    }
}
