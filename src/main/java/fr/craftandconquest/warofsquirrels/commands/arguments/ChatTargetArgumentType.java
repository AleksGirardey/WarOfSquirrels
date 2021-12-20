package fr.craftandconquest.warofsquirrels.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;

import java.text.ParseException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ChatTargetArgumentType implements ArgumentType<BroadCastTarget> {
    private static final Collection<String> EXAMPLES = Lists.newArrayList("GENERAL", "CITY", "FACTION");
    private static final DynamicCommandExceptionType MALFORMED_DATA = new DynamicCommandExceptionType(o -> new LiteralMessage(((ParseException) o).getMessage()));
    private static final DynamicCommandExceptionType UNKNOWN_ITEM = new DynamicCommandExceptionType(o -> new LiteralMessage("Unknown chat: " + o));
    private static final SimpleCommandExceptionType INVALID_STRING = new SimpleCommandExceptionType(new LiteralMessage("invalid value"));

    @Override
    public BroadCastTarget parse(StringReader reader) throws CommandSyntaxException {
        try {
            return BroadCastTarget.valueOf(reader.getRemaining());
        } catch (IllegalArgumentException e) {
            throw MALFORMED_DATA.createWithContext(reader, e);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (BroadCastTarget target :BroadCastTarget.values()) {
            builder.suggest(target.name());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
