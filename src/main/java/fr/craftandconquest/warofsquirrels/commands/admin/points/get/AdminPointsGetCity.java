package fr.craftandconquest.warofsquirrels.commands.admin.points.get;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsGetCity extends AdminCommandBuilder implements ICityExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("city").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City target = getArgument(context);

        player.sendMessage(ChatText.Success("City '" + target + "' has a score of " + target.getScore().getGlobalScore() + " (+" + target.getScore().getTodayScore() + ")"));

        return 0;
    }

    @Override
    public boolean isSuggestionFactionRestricted() {
        return false;
    }
}
