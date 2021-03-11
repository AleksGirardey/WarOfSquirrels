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

public class CityRemove extends CityAssistantCommandBuilder {
    private final String argumentName = "[Player]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands
                .literal("remove")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        String targetName = context.getArgument(argumentName, String.class);
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(targetName);

        if (target == null || target.getCity() == null || target.getCity().getOwner() == target || target.getCity() != player.getCity()) {
            StringTextComponent message = new StringTextComponent("Le joueur '" + targetName + "' n'existe pas ou ne peut pas être expulsé de votre ville.");
            message.applyTextStyle(TextFormatting.RED);
            player.getPlayerEntity().sendMessage(message);
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String targetName = context.getArgument(argumentName, String.class);
        Player target = WarOfSquirrels.instance.getPlayerHandler().get(targetName);

        player.getCity().removeCitizen(target, true);
        return 0;
    }
}
