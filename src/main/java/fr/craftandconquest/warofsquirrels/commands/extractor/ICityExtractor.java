package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface ICityExtractor {
    String cityNameArgument = "[CityName]";

    default String getRawArgument(CommandContext<CommandSourceStack> context) {
        return context.getArgument(cityNameArgument, String.class);
    }

    default City getArgument(FullPlayer player, CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getCityHandler().getCity(getRawArgument(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getArgumentRegister() {
        return Commands.argument(cityNameArgument, StringArgumentType.string());
    }
}
