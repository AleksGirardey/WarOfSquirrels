package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CitySetPermCustom extends CityMayorOrAssistantCommandBuilder implements IPermissionExtractor {
    private final String entityArgumentName = "[target]";
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("custom")
                .then(Commands.argument(entityArgumentName, StringArgumentType.string())
                        .then(getPermissionRegister(this).executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return IPermission.getPermissionFromName(context.getArgument(entityArgumentName, String.class)) != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        IPermission target = IPermission.getPermissionFromName(context.getArgument(entityArgumentName, String.class));
        Permission permission = getPermission(context);

        WarOfSquirrels.instance.getCityHandler().SetCustomPermission(target, permission, player.getCity());

        return 0;
    }
}
