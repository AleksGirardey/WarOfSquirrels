package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CitySetPermResident extends CityAssistantCommandBuilder {
    private final String buildArgumentName = "[build]";
    private final String containerArgumentName = "[container]";
    private final String switchArgumentName = "[switch]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("resident")
                .then(Commands.argument(buildArgumentName, BoolArgumentType.bool())
                        .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                                .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())
                                        .executes(this))));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Permission permission = new Permission();

        permission.setBuild(context.getArgument(buildArgumentName, Boolean.class));
        permission.setContainer(context.getArgument(containerArgumentName, Boolean.class));
        permission.setSwitches(context.getArgument(switchArgumentName, Boolean.class));

        WarOfSquirrels.instance.getCityHandler().SetDefaultPermission(PermissionRelation.RESIDENT, permission, player.getCity());
        return 0;
    }
}
