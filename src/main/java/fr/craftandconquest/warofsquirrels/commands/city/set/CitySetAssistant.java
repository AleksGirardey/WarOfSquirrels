package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CitySetAssistant extends CityMayorCommandBuilder implements IAdminCommand, IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("assistant")
                .then(getPlayerRegister().executes(this));
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        return super.CanDoIt(player) || IsAdmin(player);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (IsAdmin(player)) return true;

        FullPlayer argument = getPlayer(context);

        return argument.getCity() == player.getCity() && player.getCity().getOwner() != argument;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer newAssistant = getPlayer(context);

        newAssistant.setAssistant(true);

        MutableComponent message = ChatText.Colored(newAssistant.getDisplayName() + " is now assistant in " + player.getCity().getDisplayName() + ".", ChatFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(newAssistant.getCity(), null, message, true);

        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.RECRUIT, PlayerExtractorType.RESIDENT);
    }
}
