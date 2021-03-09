package fr.craftandconquest.warofsquirrels.commands.city.set.perm;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CitySetPermCustom extends CityAssistantCommandBuilder {
    private final String entityArgumentName = "[target]";
    private final String buildArgumentName = "[build]";
    private final String containerArgumentName = "[container]";
    private final String switchArgumentName = "[switch]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("ally")
                .then(Commands.argument(entityArgumentName, StringArgumentType.string())
                        .then(Commands.argument(buildArgumentName, BoolArgumentType.bool())
                                .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                                        .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())
                                                .executes(this)))));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return IPermission.getPermissionFromName(context.getArgument(entityArgumentName, String.class)) != null;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        IPermission target = IPermission.getPermissionFromName(context.getArgument(entityArgumentName, String.class));
        Permission permission = new Permission();

        permission.setBuild(context.getArgument(buildArgumentName, Boolean.class));
        permission.setContainer(context.getArgument(containerArgumentName, Boolean.class));
        permission.setSwitches(context.getArgument(switchArgumentName, Boolean.class));

        if (player.getCity().getCustomPermission().containsKey(target))
            player.getCity().getCustomPermission().replace(target, permission);
        else
            player.getCity().getCustomPermission().put(target, permission);
        return 0;
    }
}
