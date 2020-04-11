package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;

public class PermissionHandler {

    enum Rights {
        SET_HOMEBLOCK
    }

    public boolean hasRightsTo(Rights rights, Object... objects) {
        switch (rights) {
            case SET_HOMEBLOCK:
                return hasRightsToSetHomeBlock((Player) objects[0], (Chunk) objects[1]);
            default:
                return false;
        }
    }

    private boolean hasRightsToSetHomeBlock(Player player, Chunk chunk) {
        return false;
    }
}
