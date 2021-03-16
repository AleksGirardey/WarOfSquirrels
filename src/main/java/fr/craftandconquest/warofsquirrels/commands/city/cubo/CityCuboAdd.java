package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityCuboAdd extends CommandBuilder implements IAdminCommand {
    private final String cuboNameArgument = "[Cubo]";
    private final String playerNameArgument = "[Player]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("add").then(
                Commands.argument(cuboNameArgument, StringArgumentType.string()).then(
                        Commands.argument(playerNameArgument, StringArgumentType.string())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        String playerName = context.getArgument(playerNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        StringTextComponent message = new StringTextComponent("");

        if (cubo == null || target == null) {
            message.appendText("Les arguments '" + cuboName + "' et '" + playerName + "' ne sont pas valides.");
            message.applyTextStyle(TextFormatting.RED);
            player.getPlayerEntity().sendMessage(message);
            return false;
        }

        if (IsAdmin(player)
                || cubo.getOwner() == player
                || (cubo.getCity() == player.getCity()
                && (cubo.getCity().getOwner() == player || player.getAssistant()))) return true;

        message.appendText("Vous ne pouvez pas ajouter quelqu'un à ce cubo.");
        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String cuboName = context.getArgument(cuboNameArgument, String.class);
        String playerName = context.getArgument(playerNameArgument, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(playerName);

        cubo.AddPlayerInList(target);
        WarOfSquirrels.instance.getCuboHandler().Save();
        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
