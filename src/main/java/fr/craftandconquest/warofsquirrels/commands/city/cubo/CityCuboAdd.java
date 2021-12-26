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

public class CityCuboAdd extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[Cubo]";
    private final String playerNameArgument = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("add").then(
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

        MutableComponent message;

        if (cubo == null || target == null) {
            player.sendMessage(
                    ChatText.Error("Les arguments '" + cuboName + "' et '" + playerName + "' ne sont pas valides."));
            return false;
        }

        if (IsAdmin(player)
                || cubo.getOwner() == player
                || (cubo.getCity() == player.getCity()
                && (cubo.getCity().getOwner() == player || player.getAssistant()))) return true;

        player.sendMessage(ChatText.Error("Vous ne pouvez pas ajouter quelqu'un à ce cubo."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        String playerName = context.getArgument(playerNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        cubo.AddPlayerInList(target);
        WarOfSquirrels.instance.getCuboHandler().Save();
        player.sendMessage(ChatText.Success("Player '" + playerName + "' has been added to cubo '" + cuboName + "' access list"));
        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
