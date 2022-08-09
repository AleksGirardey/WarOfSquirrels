package fr.craftandconquest.warofsquirrels.commands.admin.set;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.admin.AdminCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class AdminSetIgnore extends AdminCommandBuilder implements ICityExtractor {
    private final static String ignoreValue = "value";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("ignore")
                .then(getArgumentRegister()
                        .then(Commands.argument(ignoreValue, BoolArgumentType.bool())
                                .executes(this)));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = getArgument(context);

        if (city == null) {
            player.sendMessage(ChatText.Error("No city found for '" + getRawArgument(context) + "'"), true);
            return false;
        }

        return true;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = getArgument(context);

        city.setIgnore(BoolArgumentType.getBool(context, ignoreValue));

        player.sendMessage(ChatText.Success("City '" + getRawArgument(context) + "' has now Ignore set to '" + city.isIgnore() + "'"));

        return 0;
    }

    @Override
    public boolean isSuggestionFactionRestricted() {
        return false;
    }
}
