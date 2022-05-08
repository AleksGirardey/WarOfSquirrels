package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ICityExtractor {
    String cityNameArgument = "[CityName]";

    boolean isSuggestionFactionRestricted();

    default String getRawArgument(CommandContext<CommandSourceStack> context) {
        return context.getArgument(cityNameArgument, String.class);
    }

    default City getArgument(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getCityHandler().getCity(getRawArgument(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getArgumentRegister() {
        return Commands.argument(cityNameArgument, StringArgumentType.string()).suggests(getCitySuggestions());
    }

    default SuggestionProvider<CommandSourceStack> getCitySuggestions() {
        return ((context, builder) -> {
            List<City> cities;

            if (isSuggestionFactionRestricted()) {
                Player playerEntity = context.getSource().getPlayerOrException();
                FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());
                if (player.getCity() != null && player.getCity().getFaction() != null)
                    cities = WarOfSquirrels.instance.getCityHandler().getCities(player.getCity().getFaction());
                else
                    cities = WarOfSquirrels.instance.getCityHandler().getAll();
            } else
                cities = WarOfSquirrels.instance.getCityHandler().getAll();

            return suggestions(builder, cities);
        });
    }

    default CompletableFuture<Suggestions> suggestions(SuggestionsBuilder builder, List<City> cities) {
        for (City city : cities) {
            builder.suggest(city.displayName);
        }

        return builder.buildFuture();
    }
}
