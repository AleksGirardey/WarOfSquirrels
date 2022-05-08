package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;


public class CityInfo extends CommandBuilder implements ICityExtractor {
    private final static CityInfo CMD_NO_ARGS = new CityInfo(false);
    private final static CityInfo CMD_ARGS = new CityInfo(true);

    private final boolean args;

    public CityInfo() {
        args = false;
    }

    public CityInfo(boolean hasArgs) {
        args = hasArgs;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info").executes(CMD_NO_ARGS).then(getArgumentRegister().executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;

        if (args) {
            city = getArgument(context);
        } else {
            city = player.getCity();
        }

        if (city == null) {
            player.sendMessage(ChatText.Error("You do not belong to a city or the one in argument doesn't exist."));
        }

        return city != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;

        if (args)
            city = getArgument(context);
        else
            city = player.getCity();

        city.displayInfo(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("This command requires an argument if you do not belong to a city")
                .withStyle(ChatFormatting.BOLD);
    }

    @Override
    public boolean isSuggestionFactionRestricted() { return false; }
}
