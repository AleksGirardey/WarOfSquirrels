package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CityCuboDelete extends CityCommandBuilder implements IAdminCommand {
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
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(argumentName, String.class));

        if (cubo != null && IsAdmin(player) || (player.getCity().getOwner() == player || player.getAssistant()))
            return true;

        return cubo != null && cubo.getOwner() == player;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String cuboName = context.getArgument(argumentName, String.class);

        if (WarOfSquirrels.instance.getCuboHandler().Delete(cuboName)) {
            player.getPlayerEntity().sendMessage(ChatText.Success("Le cubo " + cuboName + " est maintenant détruit."), Util.NIL_UUID);
        }

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return null;
    }
}
