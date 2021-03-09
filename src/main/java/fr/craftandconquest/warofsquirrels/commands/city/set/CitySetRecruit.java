package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CitySetRecruit extends CityAssistantCommandBuilder implements IAdminCommand {
    public CitySetRecruit() {};

    private final String argumentName = "[PlayerName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("recruit")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean CanDoIt(Player player) {
        return super.CanDoIt(player) || IsAdmin(player);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        if (IsAdmin(player)) return true;

        Player argument =  GetPlayerFromArguments(context);

        return argument.getCity() == player.getCity() && player.getCity().getOwner() != argument;
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        Player newRecruit = GetPlayerFromArguments(context);

        newRecruit.setResident(false);

        if (newRecruit.getAssistant())
            newRecruit.setAssistant(false);

        StringTextComponent message = new StringTextComponent(newRecruit.getDisplayName() + " is now recruit at " + player.getCity().getDisplayName() + ".");
        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newRecruit.getCity(), null, message, true);

        return 0;
    }

    private Player GetPlayerFromArguments(CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
    }
}
