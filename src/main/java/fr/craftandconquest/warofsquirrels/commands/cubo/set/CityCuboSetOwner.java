package fr.craftandconquest.warofsquirrels.commands.cubo.set;

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

import java.util.ArrayList;
import java.util.List;

public class CityCuboSetOwner extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[CuboName]";
    private final String playerNameArgument = "[PlayerName]";

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return IsAdmin(player) || super.CanDoIt(player);
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("owner").then(
                Commands.argument(cuboNameArgument, StringArgumentType.string()).then(
                        Commands.argument(playerNameArgument, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);

        if (cubo == null) {
            player.sendMessage(ChatText.Error("Cubo '" + cuboName + "' does not exist"));
            return false;
        }

        String playerName = context.getArgument(playerNameArgument, String.class);
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        if (target == null) {
            player.sendMessage(ChatText.Error("Player '" + playerName + "' does not exist"));
            return false;
        }

        List<FullPlayer> list = new ArrayList<>();

        list.add(cubo.getOwner());
        list.add(cubo.getCity().getOwner());
        list.addAll(cubo.getCity().getAssistants());

        if (list.contains(player))
            return true;

        player.sendMessage(ChatText.Error("Vous ne pouvez pas modifier les permissions de ce cubo."));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(cuboNameArgument, String.class));
        FullPlayer target = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(playerNameArgument, String.class));

        cubo.setOwner(target);
        WarOfSquirrels.instance.getCuboHandler().Save();

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
