package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;


public class CityInfo extends CommandBuilder {
    private final String cityNameArgument = "[City]";

    private final static CityInfo CMD_NO_ARGS = new CityInfo(false);
    private final static CityInfo CMD_ARGS = new CityInfo(true);

    private final boolean args;
    private String targetName;

    public CityInfo() {
        args = false;
    }

    public CityInfo(boolean hasArgs) {
        args = hasArgs;
    }

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("info")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument(cityNameArgument, StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        String target;
        City city;

        if (args) {
            target = context.getArgument(cityNameArgument, String.class);
            city = WarOfSquirrels.instance.getCityHandler().getCity(target);
        } else {
            city = player.getCity();
        }

        return city != null;
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;

        if (args) {
            targetName = context.getArgument(cityNameArgument, String.class);
            city = WarOfSquirrels.instance.getCityHandler().getCity(targetName);
            WarOfSquirrels.LOGGER.info("[WoS][Debug] City Info with args");
        }
        else {
            city = player.getCity();
            WarOfSquirrels.LOGGER.info("[WoS][Debug] City Info without args");
        }

        city.displayInfo(player);

        return 0;
    }

    @Override
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("This command requires an argument if you do not belong to a city")
                .withStyle(ChatFormatting.BOLD);
    }
}
