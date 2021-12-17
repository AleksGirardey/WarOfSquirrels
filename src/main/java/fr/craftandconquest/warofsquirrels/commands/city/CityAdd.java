package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityAdd extends CityAssistantCommandBuilder {
    private final String argumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("add")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String argument = context.getArgument(argumentName, String.class);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(argument);
        MutableComponent message;
        boolean ret = true;

        if (target == null) {
            message = ChatText.Error("Le joueur " + argument + " n'existe pas.");
            ret = false;
        } else if (target.getCity() != null) {
            message = ChatText.Error("Le joueur " + target.getDisplayName() + " appartient déjà à une ville.");
            ret = false;
        } else
            message = ChatText.Error("Unknown error");

        if (ret) return true;

        player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
        player.getCity().addCitizen(target);
        return 0;
    }
}
