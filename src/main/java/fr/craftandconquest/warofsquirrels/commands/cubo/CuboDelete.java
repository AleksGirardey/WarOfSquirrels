package fr.craftandconquest.warofsquirrels.commands.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CuboDelete extends CityCommandBuilder implements IAdminCommand {
    private final String argumentName = "[CuboName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands
                .literal("delete")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(argumentName, String.class);
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(cuboName);

        if (cubo == null) {
            player.sendMessage(ChatText.Error("Cubo '" + cuboName + "' does not exist."));
            return false;
        }

        if (IsAdmin(player) || cubo.getCity().getOwner().equals(player) || (cubo.getCity().equals(player.getCity()) && player.getAssistant()))
            return true;

        if (cubo.getOwner().equals(player)) return true;

        player.sendMessage(ChatText.Error("You cannot delete the cubo '" + cuboName + "'"));
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(argumentName, String.class);

        if (WarOfSquirrels.instance.getCuboHandler().Delete(cuboName)) {
            player.sendMessage(ChatText.Success("Cubo " + cuboName + " has been destroyed."));
        }

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
