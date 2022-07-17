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
import fr.craftandconquest.warofsquirrels.object.faction.guild.Guild;
import lombok.SneakyThrows;
import net.minecraft.commands.SharedSuggestionProvider;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class GuildArgumentType implements ArgumentType<Guild> {
    private final GuildType type;

    private GuildArgumentType(final GuildType type) {
        this.type = type;
    }

    public static GuildArgumentType guild() {
        return new GuildArgumentType(GuildType.NORMAL);
    }

    public static String getString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public GuildType getType() {
        return type;
    }

    @Override
    public Guild parse(StringReader reader) throws CommandSyntaxException {
        String guildName = reader.readString();
        Guild guild = WarOfSquirrels.instance.getGuildHandler().get(guildName);

        if (guildName == null)
            throw new SimpleCommandExceptionType(new LiteralMessage("Cannot found a guild with that name.")).create();

        return guild;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(WarOfSquirrels.instance.getGuildHandler().getAllAsCollection(), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return getCities();
    }

    private static Collection<String> getCities() {
        return WarOfSquirrels.instance.getGuildHandler().getAllAsCollection();
    }

    public enum GuildType {
        NORMAL(GuildArgumentType::getCities);

        private final Callable<Collection<String>> examples;

        GuildType(Callable<Collection<String>> function) {
            this.examples = function;
        }

        @SneakyThrows
        public Collection<String> getExamples() {
            return examples.call();
        }
    }
}
