package fr.craftandconquest.warofsquirrels.commands.admin.points.add;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsAddFaction extends AdminCommandBuilder implements IFactionExtractor {
    private final String amountArgument = "amount";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("faction")
                .then(Commands.argument(amountArgument, IntegerArgumentType.integer())
                        .then(getFactionRegister().executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction target = getFaction(context);
        int amount = IntegerArgumentType.getInteger(context, amountArgument);

        target.getScore().AddScore(amount);

        player.sendMessage(ChatText.Success("Faction '" + target.getDisplayName() + "' received '" + amount + "' for a total score of '"
                + target.getScore().getGlobalScore() + " (+" + target.getScore().getTodayScore() + ")'"));

        return 0;
    }
}
