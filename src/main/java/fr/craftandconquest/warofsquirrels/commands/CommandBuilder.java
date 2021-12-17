package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

public abstract class CommandBuilder implements Command<CommandSourceStack>, IAdminCommand {

    protected String errorTarget = "Not Specified";

    public abstract LiteralArgumentBuilder<CommandSourceStack> register();

    protected boolean CanDoIt(FullPlayer player) {
        return true;
    }

    protected abstract boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context);

    protected abstract int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context);

    protected abstract MutableComponent ErrorMessage();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        net.minecraft.world.entity.player.Player playerEntity = context.getSource().getPlayerOrException();
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (IsAdmin(player) || (CanDoIt(player) && SpecialCheck(player, context)))
            return ExecCommand(player, context);

        if (ErrorMessage() != null) {
            player.getPlayerEntity().sendMessage(ErrorMessage()/*.append(" : " + errorTarget)*/, Util.NIL_UUID);
        }
        return -1;
    }
}
