package fr.craftandconquest.warofsquirrels.commands.admin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class AdminCreateFakePlayer extends AdminCommandBuilder {
    private final String argumentName = "FakeName";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("createFakePlayer")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    player.sendMessage(ChatText.Colored("== Fake Player List ==", ChatFormatting.GRAY), Util.NIL_UUID);

                    WarOfSquirrels.instance.getPlayerHandler().getAll().forEach(p -> {
                        if (p.isFake())
                            player.sendMessage(ChatText.Colored(p.getDisplayName(), ChatFormatting.GRAY), Util.NIL_UUID);
                    });

                    return 0;
                })
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(argumentName, String.class);
        if (WarOfSquirrels.instance.getPlayerHandler().contains(name)) {
            player.sendMessage(ChatText.Error("Player already got this name"));
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String name = context.getArgument(argumentName, String.class);

        WarOfSquirrels.instance.getPlayerHandler().CreateFakePlayer(name);

        return 0;
    }
}
