package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CitySetPermOutside extends CityAssistantCommandBuilder {
    private final String buildArgumentName = "[build]";
    private final String containerArgumentName = "[container]";
    private final String switchArgumentName = "[switch]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("outside")
                .then(Commands.argument(buildArgumentName, BoolArgumentType.bool())
                        .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                                .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())
                                        .executes(this))));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Permission permission = new Permission();

        permission.setBuild(context.getArgument(buildArgumentName, Boolean.class));
        permission.setContainer(context.getArgument(containerArgumentName, Boolean.class));
        permission.setSwitches(context.getArgument(switchArgumentName, Boolean.class));

        WarOfSquirrels.instance.getCityHandler().SetDefaultPermission(PermissionRelation.OUTSIDER, permission, player.getCity());
        return 0;
    }
}
