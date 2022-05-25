package fr.craftandconquest.warofsquirrels.commands.admin.points.add;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsAddCity extends AdminCommandBuilder implements ICityExtractor {
    private final String amountArgument = "amount";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("city")
                .then(Commands.argument(amountArgument, IntegerArgumentType.integer())
                        .then(getArgumentRegister().executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City target = getArgument(context);
        int amount = IntegerArgumentType.getInteger(context, amountArgument);

        target.getScore().AddScore(amount);

        player.sendMessage(ChatText.Success("City '" + target.getDisplayName() + "' received '" + amount + "' for a total score of '"
                + target.getScore().getGlobalScore() + " (+" + target.getScore().getTodayScore() + ")'"));

        return 0;
    }

    @Override
    public boolean isSuggestionFactionRestricted() {
        return false;
    }
}
