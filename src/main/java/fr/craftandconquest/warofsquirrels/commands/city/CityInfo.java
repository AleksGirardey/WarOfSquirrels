package fr.craftandconquest.warofsquirrels.commands.city;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.CommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CityInfo extends CommandBuilder {
    private final String cityNameArgument = "[City]";

    private final CityInfo CMD_NO_ARGS = new CityInfo(false);
    private final CityInfo CMD_ARGS = new CityInfo(true);

    private final boolean args;
    private String targetName;

    public CityInfo() { args = false; }
    public CityInfo(boolean hasArgs) { args = hasArgs; }

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("info")
                .executes(CMD_NO_ARGS)
                .then(Commands
                        .argument(cityNameArgument, StringArgumentType.string())
                        .executes(CMD_ARGS));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
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
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        City city;

        targetName = context.getArgument(cityNameArgument, String.class);

        if (args) city = WarOfSquirrels.instance.getCityHandler().getCity(targetName);
        else city = player.getCity();

        city.displayInfo(player);

        return 0;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("This command requires an argument if you do not belong to a city")
                .applyTextStyle(TextFormatting.RED)
                .applyTextStyle(TextFormatting.BOLD);
    }
}
