package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.set.perm.CitySetPerm;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface IPermissionExtractor {
    String buildArgumentName = "[Build]";
    String containerArgumentName = "[Container]";
    String switchArgumentName = "[Switch]";
    String farmArgumentName = "[Farm]";
    String interactArgumentName = "[Interact]";

    default Permission getPermission(CommandContext<CommandSourceStack> context) {
        Permission perm = new Permission();
        perm.setBuild(context.getArgument(buildArgumentName, boolean.class));
        perm.setContainer(context.getArgument(containerArgumentName, boolean.class));
        perm.setSwitches(context.getArgument(switchArgumentName, boolean.class));
        perm.setFarm(context.getArgument(farmArgumentName, boolean.class));
        perm.setInteract(context.getArgument(interactArgumentName, boolean.class));

        return perm;
    }

    default RequiredArgumentBuilder<CommandSourceStack, Boolean> getPermissionRegister(Command<CommandSourceStack> citySetPerm) {
        return Commands.argument(buildArgumentName, BoolArgumentType.bool())
                .then(Commands.argument(containerArgumentName, BoolArgumentType.bool())
                        .then(Commands.argument(switchArgumentName, BoolArgumentType.bool())
                                .then(Commands.argument(farmArgumentName, BoolArgumentType.bool())
                                        .then(Commands.argument(interactArgumentName, BoolArgumentType.bool()).executes(citySetPerm)))));
    }
}
