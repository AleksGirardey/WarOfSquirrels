package fr.craftandconquest.warofsquirrels.commands.register;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.admin.*;
import fr.craftandconquest.warofsquirrels.commands.admin.points.AdminPoints;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class AdminCommandRegister extends CommandBuilder implements ICommandRegister {
    private final AdminChunkInfoCommand adminChunkInfoCommand = new AdminChunkInfoCommand();
    private final AdminForceDailyUpdate adminForceDailyUpdate = new AdminForceDailyUpdate();
    private final AdminCreate adminCreate = new AdminCreate();
    private final AdminTp adminTp = new AdminTp();
    private final AdminWhitelist adminWhitelist = new AdminWhitelist();
    private final AdminTerritory adminTerritory = new AdminTerritory();
    private final AdminSet adminSet = new AdminSet();
    private final AdminPoints adminPoints = new AdminPoints();
    private final AdminLastUpdate adminLastUpdate = new AdminLastUpdate();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {}

    public void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
        dispatcher.register(Commands.literal("admin")
                .then(adminChunkInfoCommand.register())
                .then(adminForceDailyUpdate.register())
                .then(adminCreate.register(context))
                .then(adminTp.register())
                .then(adminWhitelist.register())
                .then(adminTerritory.register())
                .then(adminSet.register())
                .then(adminPoints.register())
                .then(adminLastUpdate.register())
                .executes(this));
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
            message = ChatText.Colored("Admin mode activated.", ChatFormatting.GOLD);
        else
            message = ChatText.Colored("Admin mode deactivated", ChatFormatting.GOLD);

        player.sendMessage(message);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
