package fr.craftandconquest.warofsquirrels.commands.admin.points.get;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IFactionExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminPointsGetFaction extends AdminCommandBuilder implements IFactionExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("faction").then(getFactionRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        Faction target = getFaction(context);

        player.sendMessage(ChatText.Success("Faction '" + target + "' has a score of " + target.getScore().getGlobalScore() + " (+" + target.getScore().getTodayScore() + ")"));

        return 0;
    }
}
