package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public abstract class CitySetPerm extends CityMayorOrAssistantCommandBuilder implements IPermissionExtractor {
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
        WarOfSquirrels.LOGGER.info("[WoS][Debug] set perm 1");
        Permission permission = getPermission(context);
        WarOfSquirrels.LOGGER.info("[WoS][Debug] set perm 2");

        WarOfSquirrels.instance.getCityHandler().SetDefaultPermission(getPermissionRelation(), permission, player.getCity());
        WarOfSquirrels.LOGGER.info("[WoS][Debug] set perm 3");

        return 0;
    }
}
