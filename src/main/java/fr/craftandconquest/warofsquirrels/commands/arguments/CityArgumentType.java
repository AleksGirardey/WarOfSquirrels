package fr.craftandconquest.warofsquirrels.commands.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import lombok.SneakyThrows;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class CityArgumentType implements ArgumentType<City> {
    private final CityType type;

    private CityArgumentType(final CityType type) {
        this.type = type;
    }

    public static CityArgumentType cityAtWar() {
        return new CityArgumentType(CityType.AT_WAR);
    }

    public static CityArgumentType city() {
        return new CityArgumentType(CityType.NORMAL);
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public CityType getType() {
        return type;
    }

    @Override
    public City parse(StringReader reader) throws CommandSyntaxException {
        String cityName = reader.readString();
        City city = WarOfSquirrels.instance.getCityHandler().getCity(cityName);

        if (city == null)
            throw new SimpleCommandExceptionType(new LiteralMessage("Cannot found a city with that name.")).create();

        return city;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return null;
    }

    @Override
    public Collection<String> getExamples() {
        return null;
    }

    private static Collection<String> getCitiesAtWar() {
        return WarOfSquirrels.instance.getWarHandler().getCitiesList();
    }

    private static Collection<String> getCities() {
        return WarOfSquirrels.instance.getCityHandler().getAllAsCollection();
    }

    public enum CityType {
        AT_WAR(CityArgumentType::getCitiesAtWar),
        NORMAL(CityArgumentType::getCities);

        private final Callable<Collection<String>> examples;

        CityType(Callable<Collection<String>> function) {
            this.examples = function;
        }

        @SneakyThrows
        public Collection<String> getExamples() {
            return examples.call();
        }
    }
}
