package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminChunkInfoCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class AdminCommandRegister extends CommandBuilder implements ICommandRegister {
    private final AdminChunkInfoCommand adminChunkInfoCommand = new AdminChunkInfoCommand();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("admin")
                .then(adminChunkInfoCommand.register())
                .executes(this)
        );
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return null;
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return player.getPlayerEntity().hasPermissions(2);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        player.setAdminMode(!player.isAdminMode());

        MutableComponent message;

        if (player.isAdminMode())
            message = ChatText.Colored("Admin mode activé.", ChatFormatting.GOLD);
        else
            message = ChatText.Colored("Admin mode désactivé", ChatFormatting.GOLD);

        player.sendMessage(message);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
