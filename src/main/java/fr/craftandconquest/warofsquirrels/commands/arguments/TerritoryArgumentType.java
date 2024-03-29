package fr.craftandconquest.warofsquirrels.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class TerritoryArgumentType implements ArgumentType<Territory>  {
    @Override
    public Territory parse(StringReader reader) throws CommandSyntaxException {
        return WarOfSquirrels.instance.getTerritoryHandler().get(reader.readString());
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
//        List<Territory> territories = WarOfSquirrels.instance.getTerritoryHandler().getAll();
//
//        for (Territory territory : territories) {
//            builder.suggest(territory.getName());
//        }
//        return builder.buildFuture();

        List<Territory> territories = WarOfSquirrels.instance.getTerritoryHandler().getAll();
        List<String> names = new ArrayList<>(territories.stream().map(Territory::getDisplayName).toList());

        return SharedSuggestionProvider.suggest(names, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return Arrays.asList("Aarvan", "Elyphis", "Oberon");
    }
}
