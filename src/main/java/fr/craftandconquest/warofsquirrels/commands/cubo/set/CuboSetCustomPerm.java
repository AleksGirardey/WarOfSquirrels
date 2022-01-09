package fr.craftandconquest.warofsquirrels.commands.cubo.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPermissionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class CuboSetCustomPerm extends CommandBuilder implements IPermissionExtractor, IAdminCommand {
    private final String cuboNameArgument = "[CuboName]";
    private final String playerNameArgument = "[TargetName]";

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return IsAdmin(player) || super.CanDoIt(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("customperm").then(Commands
                .argument(cuboNameArgument, StringArgumentType.string())
                .then(Commands.argument(playerNameArgument, StringArgumentType.string())
                        .then(getPermissionRegister(this))));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));

        if (cubo == null) return false;

        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(playerNameArgument, String.class));

        if (target == null) return false;

        List<FullPlayer> list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;

        player.sendMessage(ChatText.Error("You cannot change permissions of this cubo."));
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(playerNameArgument, String.class));
        Permission permission = getPermission(context);

        cubo.AddPlayerCustomPermission(target, permission);
        WarOfSquirrels.instance.getCuboHandler().Save();
        MutableComponent text = ChatText.Success("Custom permissions for player '" + target.getDisplayName() + "' on cubo '" + cubo.getName() + "' are now " + permission);

        player.sendMessage(text);

        if (!cubo.getOwner().equals(player))
            cubo.getOwner().sendMessage(text);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot use this command.");
    }
}
