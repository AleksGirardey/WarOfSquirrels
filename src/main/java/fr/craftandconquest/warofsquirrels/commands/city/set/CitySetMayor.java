package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class CitySetMayor extends CityMayorCommandBuilder implements IAdminCommand {
    private final CitySetMayor CMD_NO_CITY = new CitySetMayor(false);
    private final CitySetMayor CMD_CITY = new CitySetMayor(true);

    private CitySetMayor(boolean cityArg) { hasCityArg = cityArg; }
    public CitySetMayor() {};

    private boolean hasCityArg;

    private final String argumentName = "[PlayerName]";
    private final String argumentCityName = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return Commands.literal("mayor")
                .then(Commands
                        .argument(argumentName, StringArgumentType.string())
                        .executes(CMD_NO_CITY)
                        .then(Commands
                                .argument(argumentCityName, StringArgumentType.string())
                                .executes(CMD_CITY)));
    }

    @Override
    protected boolean CanDoIt(Player player) {
        if (!hasCityArg)
            return super.CanDoIt(player) || IsAdmin(player);
        else
            return IsAdmin(player);
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        return IsAdmin(player) || WarOfSquirrels.instance.getPlayerHandler()
                .get(context.getArgument(argumentName, String.class))
                .getCity() == player.getCity();
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        City city;
        if (!hasCityArg)
            city = player.getCity();
        else
            city = WarOfSquirrels.instance.getCityHandler().getCity(context.getArgument(argumentCityName, String.class));
        Player newMayor = WarOfSquirrels.instance.getPlayerHandler().get(context.getArgument(argumentName, String.class));
        Player oldMayor = city.getOwner();

        city.SetOwner(newMayor);
        newMayor.setAssistant(false);
        oldMayor.setAssistant(true);
        StringTextComponent message = new StringTextComponent(newMayor.getDisplayName() + " is now the new leader of " + city.getDisplayName() + ".");
        message.applyTextStyle(TextFormatting.GOLD);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, message, true);

        return 0;
    }
}
