package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class CityCuboSetInPerm extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[CuboName]";
    private final String buildArgument = "[Build]";
    private final String containerArgument = "[Container]";
    private final String switchArgument = "[Switch]";

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return IsAdmin(player) || super.CanDoIt(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("inperm").then(Commands
                .argument(cuboNameArgument, StringArgumentType.string())
                .then(Commands.argument(buildArgument, BoolArgumentType.bool())
                        .then(Commands.argument(containerArgument, BoolArgumentType.bool())
                                .then(Commands.argument(switchArgument, BoolArgumentType.bool())
                                        .executes(this)))));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));

        if (cubo == null) return false;

        List<FullPlayer> list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;

        player.getPlayerEntity()
                .sendMessage(ChatText.Error("Vous ne pouvez pas modifier les permissions de ce cubo."), Util.NIL_UUID);
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));
        Permission permission = new Permission();

        permission.setBuild(context.getArgument(buildArgument, boolean.class));
        permission.setContainer(context.getArgument(containerArgument, boolean.class));
        permission.setSwitches(context.getArgument(switchArgument, boolean.class));

        cubo.setPermissionIn(permission);
        WarOfSquirrels.instance.getCuboHandler().Save();
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Vous ne pouvez pas utiliser cette commande.");
    }
}
