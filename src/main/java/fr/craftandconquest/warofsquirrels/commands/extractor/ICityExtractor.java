package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public interface ICityExtractor {
    String cityNameArgument = "[CityName]";

    default String getRawArgument(CommandContext<CommandSource> context) {
        return context.getArgument(cityNameArgument, String.class);
    }

    default City getArgument(Player player, CommandContext<CommandSource> context) {
        return WarOfSquirrels.instance.getCityHandler().getCity(getRawArgument(context));
    }

    default RequiredArgumentBuilder<CommandSource, String> getArgumentRegister() {
        return Commands.argument(cityNameArgument, StringArgumentType.string());
    }
}
