package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface IPermissionExtractor {
    String buildArgumentName = "[Build]";
    String containerArgumentName = "[Container]";
    String switchArgumentName = "[Switch]";
    String farmArgumentName = "[Farm]";
    String interactArgumentName = "[Interact]";

    default Permission getPermission(Player player, CommandContext<CommandSource> context) {
        Permission perm = new Permission();
        perm.setBuild(context.getArgument(buildArgumentName, boolean.class));
        perm.setContainer(context.getArgument(containerArgumentName, boolean.class));
        perm.setSwitches(context.getArgument(switchArgumentName, boolean.class));
        perm.setFarm(context.getArgument(farmArgumentName, boolean.class));
        perm.setInteract(context.getArgument(interactArgumentName, boolean.class));

        return perm;
    }

    default RequiredArgumentBuilder<CommandSource, Boolean> getPermissionRegister() {
        return Commands.argument(buildArgumentName, BoolArgumentType.bool())
                .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                        .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())
                                .then(Commands.argument(farmArgumentName, BoolArgumentType.bool())
                                        .then(Commands.argument(interactArgumentName, BoolArgumentType.bool())))));
    }
}
