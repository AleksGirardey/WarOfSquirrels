package fr.craftandconquest.warofsquirrels.commands.admin.points.add;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class AdminPointsAddPlayer extends AdminCommandBuilder implements IPlayerExtractor {
    private final String amountArgument = "amount";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("player")
                .then(Commands.argument(amountArgument, IntegerArgumentType.integer())
                        .then(getPlayerRegister().executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getPlayer(context);
        int amount = IntegerArgumentType.getInteger(context, amountArgument);

        target.getScore().AddScore(amount);

        player.sendMessage(ChatText.Success("Player '" + target.getDisplayName() + "' received '" + amount + "' for a total score of '"
                + target.getScore().getGlobalScore() + " (+" + target.getScore().getTodayScore() + ")'"));

        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.ALL);
    }
}
