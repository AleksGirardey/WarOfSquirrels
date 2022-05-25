package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;

public interface IFactionExtractor {
    String factionNameArgument = "[FactionName]";

    default String getRawFaction(CommandContext<CommandSourceStack> context) {
        return context.getArgument(factionNameArgument, String.class);
    }

    default Faction getFaction(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getFactionHandler().get(getRawFaction(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getFactionRegister() {
        return Commands.argument(factionNameArgument, StringArgumentType.string()).suggests(getSuggestions());
    }

    default SuggestionProvider<CommandSourceStack> getSuggestions() {
        return ((context, builder) -> {
            List<Faction> factions = new ArrayList<>(WarOfSquirrels.instance.getFactionHandler().getAll());

            for (Faction faction : factions) {
                builder.suggest(faction.getDisplayName());
            }

            return builder.buildFuture();
        });
    }
}
