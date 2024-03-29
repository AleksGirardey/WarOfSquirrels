package fr.craftandconquest.warofsquirrels.commands.admin.whitelist;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class AdminWhitelistCity extends AdminCommandBuilder implements IPlayerExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("city").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        if (getArgument(context) == null) {
            player.sendMessage(ChatText.Error("Wrong player"));
            return false;
        }
        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        FullPlayer target = getArgument(context);

        target.setWhitelistCityCreator(!target.isWhitelistCityCreator());

        player.sendMessage(ChatText.Success("Player " + target.getDisplayName() + " on whitelist city creator : "
                + (target.isWhitelistCityCreator() ? "TRUE" : "FALSE")));

        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.CITY_LESS);
    }
}
