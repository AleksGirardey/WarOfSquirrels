package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public interface ITerritoryExtractor {
    String territoryNameArgument = "TerritoryName";

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
            List<Territory> territories = WarOfSquirrels.instance.getTerritoryHandler().getAll();

            for (Territory territory : territories) {
                builder.suggest(territory.getName());
            }
            return builder.buildFuture();
        };
    }
}
