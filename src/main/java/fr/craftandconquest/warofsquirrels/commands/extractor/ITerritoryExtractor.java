package fr.craftandconquest.warofsquirrels.commands.extractor;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;

public interface ITerritoryExtractor {
    String territoryNameArgument = "TerritoryName";

    default String getRawTerritory(CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, territoryNameArgument);
    }

    default Territory getTerritory(CommandContext<CommandSourceStack> context) {
        return WarOfSquirrels.instance.getTerritoryHandler().get(getRawTerritory(context));
    }

    default RequiredArgumentBuilder<CommandSourceStack, String> getTerritoryRegister() {
        return Commands.argument(territoryNameArgument, StringArgumentType.string());
    }

    default Territory ExtractTerritory(FullPlayer player) {
        TerritoryHandler handler = WarOfSquirrels.instance.getTerritoryHandler();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int x = player.getPlayerEntity().getBlockX() / territorySize;
        int z = player.getPlayerEntity().getBlockZ() / territorySize;
        return handler.get(x, z);
    }
}
