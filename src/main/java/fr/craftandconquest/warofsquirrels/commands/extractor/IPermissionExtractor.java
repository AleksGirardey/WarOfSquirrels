package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface IPermissionExtractor extends IExtractor<Permission> {
    String buildArgumentName = "[Build]";
    String containerArgumentName = "[Container]";
    String switchArgumentName = "[Switch]";

    default String getRawArgument(CommandContext<CommandSource> context) {
        return "";
    }

    default Permission getArgument(Player player, CommandContext<CommandSource> context) {
        Permission perm = new Permission();
        perm.setBuild(context.getArgument(buildArgumentName, boolean.class));
        perm.setContainer(context.getArgument(containerArgumentName, boolean.class));
        perm.setSwitches(context.getArgument(switchArgumentName, boolean.class));

        return perm;
    }

    default RequiredArgumentBuilder<CommandSource, Boolean> getArgumentRegister() {
        return Commands.argument(buildArgumentName, BoolArgumentType.bool())
                .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                        .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())));
    }
}
