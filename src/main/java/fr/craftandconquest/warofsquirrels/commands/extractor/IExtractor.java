package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.object.RegistryObject;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IExtractor<T extends RegistryObject> {
    String getArgumentName();

    default String getRawArgument(CommandContext<CommandSourceStack> context) {
        return context.getArgument(getArgumentName(), String.class);
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getArgumentRegister() {
        return Commands.argument(getArgumentName(), StringArgumentType.string()).suggests(getSuggestions());
    }

    T getArgument(CommandContext<CommandSourceStack> context);
    SuggestionProvider<CommandSourceStack> getSuggestions();

    default CompletableFuture<Suggestions> suggestions(SuggestionsBuilder builder, List<T> values) {
        for (T value : values)
            builder.suggest(value.getDisplayName());

        return builder.buildFuture();
    }
}
