package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class CitySetMayor extends CityMayorCommandBuilder implements IAdminCommand, IPlayerExtractor {
    private static final CitySetMayor CMD_NO_CITY = new CitySetMayor(false);
    private static final CitySetMayor CMD_CITY = new CitySetMayor(true);
    private final static String cityArgumentName = "city";
    private CitySetMayor(boolean cityArg) {
        hasCityArg = cityArg;
    }

    public CitySetMayor() {}

    private boolean hasCityArg;

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("mayor")
                .then(getArgumentRegister()
                        .executes(CMD_NO_CITY)
                        .then(Commands.argument(cityArgumentName, StringArgumentType.string())
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
        return IsAdmin(player) ||
                (player.getCity() != null && getArgument(context).getCity().equals(player.getCity()));
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;
        if (!hasCityArg)
            city = player.getCity();
        else
            city = WarOfSquirrels.instance.getCityHandler().get(StringArgumentType.getString(context, cityArgumentName));
        FullPlayer newMayor = getArgument(context);
        FullPlayer oldMayor = city.getOwner();

        if (hasCityArg) {
            boolean newMayorHasCity = newMayor.getCity() != null;
            boolean newMayorSameCity = newMayorHasCity && newMayor.getCity() == city;

            if (newMayorHasCity) {
                if (newMayor.getCity().getOwner().equals(newMayor)) {
                    newMayor.sendMessage(ChatText.Success("You cannot leave your mayor position"));
                    return -1;
                }

                if (!newMayorSameCity) {
                    newMayor.getCity().removeCitizen(newMayor, true);
                }
            }

            city.addCitizen(newMayor);
        }

        city.SetOwner(newMayor);
        newMayor.setAssistant(false);
        oldMayor.setAssistant(true);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, ChatText.Colored(newMayor.getDisplayName() + " is now the new leader of " + city.getDisplayName() + ".", ChatFormatting.GOLD), true);

        return 0;
    }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return hasCityArg ? List.of(PlayerExtractorType.ALL) : List.of(PlayerExtractorType.CITIZENS);
    }
}
