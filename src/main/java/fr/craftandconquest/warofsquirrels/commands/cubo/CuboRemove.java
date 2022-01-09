package fr.craftandconquest.warofsquirrels.commands.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CuboRemove extends CommandBuilder implements IAdminCommand {
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
            player.sendMessage(ChatText.Error("Arguments '" + cuboName + "' and/or '" + playerName + "' are not valid."));
            return false;
        }

        if ((cubo.getCity().getOwner().equals(player) || (player.getAssistant() && player.getCity().equals(cubo.getCity())) || IsAdmin(player))) return true;
        if (cubo.getOwner().equals(player)) return true;

        player.sendMessage(ChatText.Error("You cannot remove someone from this cubo."));
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

        player.sendMessage(ChatText.Success("Player '" + playerName + "' has been removed from cubo '" + cuboName + "'"));

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
