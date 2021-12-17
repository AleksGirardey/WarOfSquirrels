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

public class CityRemove extends CityAssistantCommandBuilder {
    private final String argumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("remove")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String targetName = context.getArgument(argumentName, String.class);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(targetName);

        if (target == null || target.getCity() == null || target.getCity().getOwner() == target || target.getCity() != player.getCity()) {
            MutableComponent message = ChatText.Error("Le joueur '" + targetName + "' n'existe pas ou ne peut pas être expulsé de votre ville.");
            player.getPlayerEntity().sendMessage(message, Util.NIL_UUID);
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String targetName = context.getArgument(argumentName, String.class);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(targetName);

        player.getCity().removeCitizen(target, true);
        return 0;
    }
}
