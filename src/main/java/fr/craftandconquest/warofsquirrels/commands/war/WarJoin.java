package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.handler.FactionHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.War;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class WarJoin extends CityCommandBuilder {
    @Override
    public LiteralArgumentBuilder<CommandSource> register() {
        return literal("join").then(
                argument("cityName", StringArgumentType.string())
                .executes(this));
    }

    @Override
    protected boolean SpecialCheck(Player player, CommandContext<CommandSource> context) {
        String cityName = context.getArgument("cityName", String.class);
        City city = WarOfSquirrels.instance.getCityHandler().getCity(cityName);
        City playerCity = player.getCity();

        if (city == null) {
            player.getPlayerEntity().sendMessage(
                    new StringTextComponent("The city '" + cityName + "' does not exist")
            .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD));
            return false;
        }

        War war = WarOfSquirrels.instance.getWarHandler().getWar(city);

        if (war == null) {
            player.getPlayerEntity().sendMessage(
                    new StringTextComponent("The city '" + cityName + "' is not at war")
                    .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD));
            return false;
        }

        FactionHandler fh = WarOfSquirrels.instance.getFactionHandler();

        return (fh.areAllies(war.getCityAttacker().getFaction(), playerCity.getFaction())
                || fh.areAllies(war.getCityDefender().getFaction(), playerCity.getFaction())
                || war.getCityAttacker().getFaction() == playerCity.getFaction()
                || war.getCityDefender().getFaction() == playerCity.getFaction()
                || war.getCityAttacker() == playerCity
                || war.getCityDefender() == playerCity
                && (!war.getAttackers().contains(player) && !war.getDefenders().contains(player)));
    }

    @Override
    protected int ExecCommand(Player player, CommandContext<CommandSource> context) {
        String cityName = context.getArgument("cityName", String.class);
        City city = WarOfSquirrels.instance.getCityHandler().getCity(cityName);
        City playerCity = player.getCity();
        War war = WarOfSquirrels.instance.getWarHandler().getWar(city);

        FactionHandler fh = WarOfSquirrels.instance.getFactionHandler();

        if (fh.areAllies(war.getCityAttacker().getFaction(), playerCity.getFaction())
                || playerCity == war.getCityAttacker() || playerCity.getFaction() == war.getCityAttacker().getFaction())
            war.AddAttacker(player);
        else
            war.AddDefender(player);
        return 1;
    }

    @Override
    protected ITextComponent ErrorMessage() {
        return new StringTextComponent("You cannot join this war.")
                .applyTextStyle(TextFormatting.RED).applyTextStyle(TextFormatting.BOLD);
    }
}
