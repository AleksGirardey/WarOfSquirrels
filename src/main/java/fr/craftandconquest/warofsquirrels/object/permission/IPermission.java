package fr.craftandconquest.warofsquirrels.object.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;

import java.util.UUID;

public interface IPermission {
    @JsonIgnore
    UUID getUuid();

    @JsonIgnore
    PermissionTarget getPermissionTarget();

    @JsonIgnore
    String getPermissionDisplayName();

    @JsonIgnore
    String displayPermissions();

    @JsonIgnore
    EPermissionType getPermissionType();

    enum EPermissionType {
        PLAYER,
        CITY,
        FACTION,
        GUILD,
    }

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
