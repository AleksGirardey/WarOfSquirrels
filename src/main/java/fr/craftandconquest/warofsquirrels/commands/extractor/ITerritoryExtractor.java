package fr.craftandconquest.warofsquirrels.commands.extractor;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.world.Territory;

public interface ITerritoryExtractor {
    default Territory ExtractTerritory(FullPlayer player) {
        TerritoryHandler handler = WarOfSquirrels.instance.getTerritoryHandler();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int x = player.getPlayerEntity().getBlockX() / territorySize;
        int z = player.getPlayerEntity().getBlockZ() / territorySize;
        return handler.get(x, z);
    }
}
