package fr.craftandconquest.warofsquirrels.commands.city.cubo.set;

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
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class CityCuboSetOutPerm extends CommandBuilder implements IPermissionExtractor, IAdminCommand {
    private final String cuboNameArgument = "[CuboName]";

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return IsAdmin(player) || super.CanDoIt(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("outperm").then(Commands
                .argument(cuboNameArgument, StringArgumentType.string())
                .then(getPermissionRegister(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C1");
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C2");

        if (cubo == null) return false;
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C3");

        List<FullPlayer> list = new ArrayList<>();

        list.add(cubo.getOwner());
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C4");
        list.add(cubo.getCity().getOwner());
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C5");
        list.addAll(cubo.getCity().getAssistants());
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C6");

        if (list.contains(player))
            return true;

        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm C7");
        player.getPlayerEntity()
                .sendMessage(ChatText.Error("Vous ne pouvez pas modifier les permissions de ce cubo."), Util.NIL_UUID);
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm E1");
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm E2");
        Permission permission = getPermission(context);
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm E3");

        cubo.setPermissionOut(permission);
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm E4");
        WarOfSquirrels.instance.getCuboHandler().Save();
        WarOfSquirrels.LOGGER.info("[WoS][Debug] Cubo SetOutPerm E5");
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("Vous ne pouvez pas utiliser cette commande.");
    }
}
