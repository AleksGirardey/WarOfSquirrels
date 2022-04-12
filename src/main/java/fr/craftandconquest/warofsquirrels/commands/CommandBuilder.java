package fr.craftandconquest.warofsquirrels.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.SneakyThrows;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;

public abstract class CommandBuilder implements Command<CommandSourceStack>, IAdminCommand {

    protected String errorMessage = "";

    public abstract LiteralArgumentBuilder<CommandSourceStack> register();

    protected boolean CanDoIt(FullPlayer player) {
        return true;
    }

    protected abstract boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context);

    protected abstract int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context);

    protected abstract MutableComponent ErrorMessage();

    @SneakyThrows
    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        net.minecraft.world.entity.player.Player playerEntity = context.getSource().getPlayerOrException();
        FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());

        if (IsAdmin(player)) {
            player.sendMessage(ChatText.Colored("Forced command as admin", ChatFormatting.GOLD));
            return ExecCommand(player, context);
        }

        if (!CanDoIt(player)) {
            if (ErrorMessage() != null) {
                player.sendMessage(ErrorMessage());
            }
            return -1;
        }

        if (SpecialCheck(player, context))
            return ExecCommand(player, context);

        return -1;
    }
}
