package fr.craftandconquest.warofsquirrels.commands.city.set;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.IAdminCommand;
import fr.craftandconquest.warofsquirrels.commands.city.CityMayorCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.IPlayerExtractor;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public class CitySetMayor extends CityMayorCommandBuilder implements IAdminCommand, IPlayerExtractor, ICityExtractor {
    private static final CitySetMayor CMD_NO_CITY = new CitySetMayor(false);
    private static final CitySetMayor CMD_CITY = new CitySetMayor(true);

    private CitySetMayor(boolean cityArg) {
        hasCityArg = cityArg;
    }

    public CitySetMayor() {}

    private boolean hasCityArg;
    private final String argumentCityName = "[CityName]";

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("mayor")
                .then(getPlayerRegister().executes(CMD_NO_CITY)
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
        return IsAdmin(player) || (player.getCity() != null && getPlayer(context).getCity().equals(player.getCity()));
    }

    @Override
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city;
        if (!hasCityArg)
            city = player.getCity();
        else
            city = WarOfSquirrels.instance.getCityHandler().getCity(context.getArgument(argumentCityName, String.class));
        FullPlayer newMayor = getPlayer(context);
        FullPlayer oldMayor = city.getOwner();

        if (hasCityArg) {
            if (newMayor.getCity() != null && newMayor.getCity() != city) {
                if (!newMayor.getCity().getOwner().equals(newMayor)) {
                    newMayor.getCity().removeCitizen(newMayor, true);
                    city.addCitizen(newMayor);
                } else {
                    newMayor.sendMessage(ChatText.Error("Cannot force set mayor if you are mayor"));
                    return -1;
                }
            }
        }

        city.SetOwner(newMayor);
        newMayor.setAssistant(false);
        oldMayor.setAssistant(true);

        WarOfSquirrels.instance.getBroadCastHandler().BroadCastMessage(city, null, ChatText.Colored(newMayor.getDisplayName() + " is now the new leader of " + city.getDisplayName() + ".", ChatFormatting.GOLD), true);

        return 0;
    }

    @Override
    public boolean isSuggestionFactionRestricted() { return false; }

    @Override
    public List<PlayerExtractorType> getTargetSuggestionTypes() {
        return List.of(PlayerExtractorType.CITIZENS);
    }
}
