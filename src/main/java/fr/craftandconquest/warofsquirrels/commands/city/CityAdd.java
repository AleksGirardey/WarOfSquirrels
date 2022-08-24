package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

public class CityAdd extends CityMayorOrAssistantCommandBuilder implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("add").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);
        MutableComponent message;
        boolean ret = true;

        if (target == null) {
            message = ChatText.Error("Player " + getRawArgument(context) + " do not exist.");
            ret = false;
        } else if (target.getCity() != null) {
            message = ChatText.Error("Player " + target.getDisplayName() + " already belong to a city.");
            ret = false;
        } else if (WarOfSquirrels.instance.getWarHandler().Contains(player.getCity())) {
            message = ChatText.Error("You cannot add someone while at war");
            ret = false;
        } else
            message = ChatText.Error("Unknown error");

        if (ret) return true;

        player.sendMessage(message, true);
        return false;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);
        player.getCity().addCitizen(target);
        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.CITY_LESS);
    }
}
