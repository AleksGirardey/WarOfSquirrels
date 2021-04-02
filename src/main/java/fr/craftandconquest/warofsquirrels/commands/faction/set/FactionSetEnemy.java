package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class FactionSetEnemy extends FactionSetDiplomacy {
    private static final FactionSetEnemy withPerm = new FactionSetEnemy(true);

    public FactionSetEnemy() {
        super();
    }

    public FactionSetEnemy(boolean hasArgs) {
        super(hasArgs);
    }

    @Override
    protected void NewDiplomacy(Player player, Faction faction, Permission perm) {
        Faction pFaction = player.getCity().getFaction();
        Annouce(pFaction, faction, "enemie");
        WarOfSquirrels.instance.getDiplomacyHandler().CreateDiplomacy(pFaction, faction, false, perm);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("enemy")
                .then(getFactionRegister()
                        .executes(this)
                        .then(getPermissionRegister()
                                .executes(withPerm)));
    }
}
