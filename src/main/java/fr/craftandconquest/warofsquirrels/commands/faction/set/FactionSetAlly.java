package fr.craftandconquest.warofsquirrels.commands.faction.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.invitation.AllianceInvitation;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class FactionSetAlly extends FactionSetDiplomacy {
    private static final FactionSetAlly withPerm = new FactionSetAlly(true);

    public FactionSetAlly() {
        super();
    }

    public FactionSetAlly(boolean hasArgs) {
        super(hasArgs);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("ally")
                .then(getFactionRegister()
                        .executes(this)
                        .then(getPermissionRegister()
                                .executes(withPerm)));
    }

    @Override
    protected void NewDiplomacy(Player player, Faction faction, Permission perm) {
        WarOfSquirrels.instance.getInvitationHandler().CreateInvitation(new AllianceInvitation(player, faction, perm));
    }
}
