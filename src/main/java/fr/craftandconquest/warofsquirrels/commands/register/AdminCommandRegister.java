package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminChunkInfoCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class AdminCommandRegister extends CommandBuilder implements ICommandRegister {
    private final AdminChunkInfoCommand adminChunkInfoCommand = new AdminChunkInfoCommand();

    @Override
    public void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("admin")
                .then(adminChunkInfoCommand.register())
                .executes(this)
        );
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() { return null; }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return player.getPlayerEntity().hasPermissionLevel(2);
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        player.setAdminMode(!player.isAdminMode());

        StringTextComponent message;

        if (player.isAdminMode())
            message = new StringTextComponent("Admin mode activé.");
        else
            message = new StringTextComponent("Admin mode désactivé");
        message.applyTextStyle(TextFormatting.GOLD);

        player.getPlayerEntity().sendMessage(message);

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
