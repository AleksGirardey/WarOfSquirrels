package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityAdd extends CityAssistantCommandBuilder {
    private final String argumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("add")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        String argument = context.getArgument(argumentName, String.class);
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(argument);
        StringTextComponent message = new StringTextComponent("");
        boolean ret = true;

        if (target == null) {
            message.appendText("Le joueur " + argument + " n'existe pas.");
            ret = false;
        }
        else if (target.getCity() != null) {
            message.appendText("Le joueur " + target.getDisplayName() + " appartient déjà à une ville.");
            ret = false;
        }

        if (ret) return true;

        message.applyTextStyle(TextFormatting.RED);
        player.getPlayerEntity().sendMessage(message);
        return false;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
        player.getCity().addCitizen(target);
        return 0;
    }
}
