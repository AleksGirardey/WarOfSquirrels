package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class CitySetRecruit extends CityMayorOrAssistantCommandBuilder implements IAdminCommand {
    public CitySetRecruit() {
    }

    private final String argumentName = "[PlayerName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("recruit")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(this));
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) || IsAdmin(player);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (IsAdmin(player)) return true;

        FullPlayer argument = GetPlayerFromArguments(context);

        return argument.getCity() == player.getCity() && player.getCity().getOwner() != argument;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer newRecruit = GetPlayerFromArguments(context);

        newRecruit.setResident(false);

        if (newRecruit.getAssistant())
            newRecruit.setAssistant(false);

        MutableComponent message = ChatText.Colored(newRecruit.getDisplayName() + " is now recruit at " + player.getCity().getDisplayName() + ".", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newRecruit.getCity(), null, message, true);

        return 0;
    }

    private FullPlayer GetPlayerFromArguments(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
    }
}
