package fr.craftandconquest.warofsquirrels.commands.faction.set.perm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.commands.faction.FactionMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class FactionSetPerm extends FactionMayorOrAssistantCommandBuilder implements IPermissionExtractor {
    protected abstract String getGroupTarget();
    protected abstract PermissionRelation getPermissionRelation();

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal(getGroupTarget()).then(getPermissionRegister(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Permission permission = getPermission(context);

        WarOfSquirrels.instance.getFactionHandler().SetDefaultPermission(getPermissionRelation(), permission, player.getCity().getFaction());

        return 0;
    }
}
