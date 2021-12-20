package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityCuboRemove extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[Cubo]";
    private final String playerNameArgument = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("remove").then(
                Commands.argument(cuboNameArgument, StringArgumentType.string()).then(
                        Commands.argument(playerNameArgument, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        String playerName = context.getArgument(playerNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        if (cubo == null || target == null) {
            player.getPlayerEntity().sendMessage(ChatText.Error("Les arguments '" + cuboName + "' et '" + playerName + "' ne sont pas valides."), Util.NIL_UUID);
            return false;
        }

        if (IsAdmin(player)
                || cubo.getOwner() == player
                || (cubo.getCity() == player.getCity()
                && (cubo.getCity().getOwner() == player || player.getAssistant()))) return true;

        player.getPlayerEntity().sendMessage(ChatText.Error("Vous ne pouvez pas enlever quelqu'un de ce cubo."), Util.NIL_UUID);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        String playerName = context.getArgument(playerNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        cubo.RemovePlayerInList(target);
        WarOfSquirrels.instance.getCuboHandler().Save();
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
