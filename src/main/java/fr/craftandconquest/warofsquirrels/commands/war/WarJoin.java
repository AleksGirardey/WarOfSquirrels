package fr.craftandconquest.warofsquirrels.commands.war;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.commands.city.CityCommandBuilder;
import fr.craftandconquest.warofsquirrels.commands.extractor.ICityExtractor;
import fr.craftandconquest.warofsquirrels.commands.extractor.IWarExtractor;
import fr.craftandconquest.warofsquirrels.handler.FactionHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.war.War;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;

public class WarJoin extends CityCommandBuilder implements ICityExtractor {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("join").then(getArgumentRegister().executes(this));
    }

    @Override
    protected boolean SpecialCheck(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = getArgument(context);
        City playerCity = player.getCity();

        if (city == null) {
            player.sendMessage(ChatText.Error("The city '" + getRawArgument(context) + "' does not exist")
                    .withStyle(ChatFormatting.BOLD));
            return false;
        }

        War war = WarOfSquirrels.instance.getWarHandler().getWar(city);

        if (war == null) {
            player.sendMessage(ChatText.Error("The city '" + getRawArgument(context) + "' is not at war")
                    .withStyle(ChatFormatting.BOLD));
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
    protected int ExecCommand(FullPlayer player, CommandContext<CommandSourceStack> context) {
        City city = getArgument(context);
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
    protected MutableComponent ErrorMessage() {
        return ChatText.Error("You cannot join this war.").withStyle(ChatFormatting.BOLD);
    }

    @Override
    public boolean isSuggestionFactionRestricted() {
        return true;
    }
}
