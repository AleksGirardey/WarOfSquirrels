package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CitySetMayor extends CityMayorCommandBuilder implements IAdminCommand {
    private static final CitySetMayor CMD_NO_CITY = new CitySetMayor(false);
    private static final CitySetMayor CMD_CITY = new CitySetMayor(true);

    private CitySetMayor(boolean cityArg) {
        hasCityArg = cityArg;
    }

    public CitySetMayor() {
    }

    private boolean hasCityArg;

    private final String argumentName = "[PlayerName]";
    private final String argumentCityName = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("mayor")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(CMD_NO_CITY)
                        .then(Commands
                                .argument(argumentCityName, StringArgumentType.string())
                                .executes(CMD_CITY)));
    }

    @Override
    protected boolean CanDoIt(FullPlayer player) {
        if (!hasCityArg)
            return super.CanDoIt(player) || IsAdmin(player);
        else
            return IsAdmin(player);
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return IsAdmin(player) || WarOfSquirrels.instance.getPlayerHandler()
                .get(context.getArgument(argumentName, String.class))
                .getCity() == player.getCity();
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;
        if (!hasCityArg)
            city = player.getCity();
        else
            city = WarOfSquirrels.instance.getCityHandler().getCity(context.getArgument(argumentCityName, String.class));
        FullPlayer newMayor = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
        FullPlayer oldMayor = city.getOwner();

        city.SetOwner(newMayor);
        newMayor.setAssistant(false);
        oldMayor.setAssistant(true);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, ChatText.Colored(newMayor.getDisplayName() + " is now the new leader of " + city.getDisplayName() + ".", ChatFormatting.GOLD), true);

        return 0;
    }
}
