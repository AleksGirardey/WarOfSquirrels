package fr.craftandconquest.warofsquirrels.handler;

import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;

public class PermissionHandler {

    public enum Rights {
        SET_HOMEBLOCK,
        BUILD,
        CONTAINER,
        SWITCH
    }

    public boolean hasRightsTo(Rights rights, Object... objects) {
        switch (rights) {
            case SET_HOMEBLOCK:
                return hasRightsToSetHomeBlock((Player) objects[0], (Chunk) objects[1]);
            case BUILD:
                return hasRightsToBuild();
            case CONTAINER:
                return hasRightsToContainer();
            case SWITCH:
                return hasRightsToSwitch();
            default:
                return false;
        }
    }

    private boolean hasRightsToBuild() { return false; }
    private boolean hasRightsToBuildGlobal() { return false; }
    private boolean hasRightsToBuildSpecific() { return false; }

    private boolean hasRightsToContainer() { return false; }
    private boolean hasRightsToContainerGlobal() { return false; }
    private boolean hasRightsToContainerSpecific() { return false; }

    private boolean hasRightsToSwitch() { return false; }
    private boolean hasRightsToSwitchGlobal() { return false; }
    private boolean hasRightsToSwitchSpecific() { return false; }

    private boolean hasRightsToSetHomeBlock(Player player, Chunk chunk) {
        return false;
    }
}
