package fr.craftandconquest.warofsquirrels.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Territory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TerritoryArgumentType implements ArgumentType<Territory>  {
    @Override
    public Territory parse(StringReader reader) throws CommandSyntaxException {
        Territory territory = WarOfSquirrels.instance.getTerritoryHandler().get(reader.readString());

        if (territory == null)
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException().create("Cannot find this territory");

        return territory;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<Territory> territories = WarOfSquirrels.instance.getTerritoryHandler().getAll();

        for (Territory territory : territories) {
            builder.suggest(territory.getName());
        }

        return ArgumentType.super.listSuggestions(context, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("Aarvan", "Elyphis", "Oberon");
    }
}
