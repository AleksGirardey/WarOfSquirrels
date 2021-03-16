package fr.craftandconquest.warofsquirrels.commands.city.cubo;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.cuboide.Cubo;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityCuboDelete extends CityCommandBuilder implements IAdminCommand {
    private final String argumentName = "[CuboName]";
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("delete")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        Cubo cubo = WarOfSquirrels.instance.getCuboHandler().getCubo(context.getArgument(argumentName, String.class));

        if (cubo != null && IsAdmin(player) || (player.getCity().getOwner() == player || player.getAssistant()))
            return true;

        return cubo != null && cubo.getOwner() == player;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String cuboName = context.getArgument(argumentName, String.class);

        if (WarOfSquirrels.instance.getCuboHandler().Delete(cuboName)) {
            StringTextComponent message = new StringTextComponent("Le cubo " + cuboName + " est maintenant détruit.");
            message.applyTextStyle(TextFormatting.GREEN);
            player.getPlayerEntity().sendMessage(message);
        }

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return null;
    }
}
