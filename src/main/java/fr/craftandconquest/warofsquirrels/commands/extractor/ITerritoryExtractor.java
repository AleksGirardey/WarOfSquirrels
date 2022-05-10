package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITerritoryExtractor {
    String territoryNameArgument = "TerritoryName";

    boolean suggestionIsGlobalWarTarget();
    boolean suggestionIsFactionWarTarget();

    default String getRawTerritory(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, territoryNameArgument);
    }

    default Territory getTerritory(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getTerritoryHandler().get(getRawTerritory(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getTerritoryRegister() {
        return Commands.argument(territoryNameArgument, StringArgumentType.string()).suggests(getTerritorySuggestions());
    }

    default Territory ExtractTerritory(FullPlayer player) {
        TerritoryHandler handler = WarOfSquirrels.instance.getTerritoryHandler();
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());

        return handler.get((int) territoryPos.x, (int) territoryPos.y);
    }

    default SuggestionProvider<CommandSourceStack> getTerritorySuggestions() {
        return (context, builder) -> {
            Player playerEntity;
            try {
                playerEntity = context.getSource().getPlayerOrException();
            } catch (CommandSyntaxException e) {
                return Suggestions.empty();
            }

            FullPlayer player = WarOfSquirrels.instance.getPlayerHandler().get(playerEntity.getUUID());
            List<Territory> territories;

            territories = new ArrayList<>(WarOfSquirrels.instance.getTerritoryHandler().getAll());

            if (suggestionIsFactionWarTarget()) {
                territories.removeIf(territory -> !territory.canBeReached()
                        || territory.isProtected()
                        || !(player != null && player.getCity() != null && player.getCity().getFaction() != null
                        && territory.getFaction() != null && WarOfSquirrels.instance.getDiplomacyHandler().getEnemies(player.getCity().getFaction()).contains(territory.getFaction())));
            }
            if (suggestionIsGlobalWarTarget())
                territories.removeIf(territory -> !territory.canBeReached() || territory.isProtected());

            return territorySuggestions(territories, builder);
        };
    }

    private CompletableFuture<Suggestions> territorySuggestions(List<Territory> territories, SuggestionsBuilder builder) {
        for (Territory territory : territories) {
            builder.suggest(territory.getName());
        }
        return builder.buildFuture();
    }
}
