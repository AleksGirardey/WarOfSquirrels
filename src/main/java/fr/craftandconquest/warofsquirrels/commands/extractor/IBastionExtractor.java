package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.Bastion;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.List;

public interface IBastionExtractor {
    String bastionArgumentName = "BastionName";

    default String getRawBastion(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, bastionArgumentName);
    }

    default Bastion getBastion(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getBastionHandler().get(getRawBastion(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getBastionRegister() {
        return Commands.argument(bastionArgumentName, StringArgumentType.string()).suggests(getBastionSuggestions());
    }

    default SuggestionProvider<CommandSourceStack> getBastionSuggestions() {
        return (context, builder) -> {
            List<Bastion> bastions = WarOfSquirrels.instance.getBastionHandler().getAll();

            for (Bastion bastion : bastions) {
                builder.suggest(bastion.getName());
            }
            return builder.buildFuture();
        };
    }

    private Bastion ExtractBastion(FullPlayer player) {
        Territory territory = ExtractTerritory(player);

        if (territory != null && territory.getFortification() != null && territory.getFortification().getFortificationType() == IFortification.FortificationType.BASTION)
            return (Bastion) territory.getFortification();

        return null;
    }

    private Territory ExtractTerritory(FullPlayer player) {
        TerritoryHandler handler = WarOfSquirrels.instance.getTerritoryHandler();
        Vector2 territoryPos = Utils.FromWorldToTerritory(player.getPlayerEntity().getBlockX(), player.getPlayerEntity().getBlockZ());

        return handler.get((int) territoryPos.x, (int) territoryPos.y);
    }
}
