package fr.craftandconquest.warofsquirrels.object.permission;

import fr.craftandconquest.warofsquirrels.WarOfSquirrels;

public interface IPermission {
    PermissionTarget getPermissionTarget();

    static IPermission getPermissionFromName(String entityName) {
        if (WarOfSquirrels.instance.getPlayerHandler().get(entityName) != null)
            return WarOfSquirrels.instance.getPlayerHandler().get(entityName);
        else if (WarOfSquirrels.instance.getCityHandler().getCity(entityName) != null)
            return WarOfSquirrels.instance.getCityHandler().getCity(entityName);
        else if (WarOfSquirrels.instance.getFactionHandler().get(entityName) != null)
            return WarOfSquirrels.instance.getFactionHandler().get(entityName);
        return null;
    }
}
