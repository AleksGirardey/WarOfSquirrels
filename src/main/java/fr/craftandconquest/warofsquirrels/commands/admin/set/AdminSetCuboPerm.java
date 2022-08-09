package fr.craftandconquest.warofsquirrels.commands.admin.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.AdminCubo;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminSetCuboPerm extends AdminCommandBuilder implements IPermissionExtractor {
    private final String cuboNameArgument = "cubo";
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("cuboperm").then(
                Commands.argument(cuboNameArgument, StringArgumentType.string())
                        .then(getPermissionRegister(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (WarOfSquirrels.instance.getAdminHandler().get(StringArgumentType.getString(context, cuboNameArgument)) != null)
            return true;

        player.sendMessage(ChatText.Error("Cubo doesn't exist"), true);

        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Permission permission = getPermission(context);
        AdminCubo cubo = WarOfSquirrels.instance.getAdminHandler().get(StringArgumentType.getString(context, cuboNameArgument));

        cubo.setPermission(permission);

        player.sendMessage(ChatText.Success("Cubo has now new permissions"));

        return 1;
    }
}
