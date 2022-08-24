package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorOrAssistantCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class WarSetTarget extends CityMayorOrAssistantCommandBuilder implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("target").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);
        return target != null
                && war.getState().equals(War.WarState.Preparation)
                && target.getCity() != null
                && war.getCityDefender().equals(target.getCity())
                && war.isDefender(target);
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        War war = WarOfSquirrels.instance.getWarHandler().getWar(player);
        FullPlayer target = getArgument(context);

        war.setTarget(target);
        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(
                war, null, ChatText.Colored("Defenders as set a new war target : '" + target + "'", ChatFormatting.GOLD), true);
        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() { return List.of(PlayerExtractorType.CITIZENS); }
}