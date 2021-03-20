package fr.craftandconquest.warofsquirrels.commands.register;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.TerritoryHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.world.Territory;

public interface ITerritoryExtractor {
    default Territory ExtractTerritory(Player player) {
        TerritoryHandler handler = WarOfSquirrels.instance.getTerritoryHandler();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int x = player.getPlayerEntity().getPosition().getX() / territorySize;
        int z = player.getPlayerEntity().getPosition().getZ() / territorySize;
        return handler.get(x, z, player.getPlayerEntity().dimension.getId());
    }
}
