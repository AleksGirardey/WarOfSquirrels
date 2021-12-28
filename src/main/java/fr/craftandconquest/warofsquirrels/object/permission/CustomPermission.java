package fr.craftandconquest.warofsquirrels.object.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class CustomPermission {
    @JsonProperty @Getter @Setter
    public UUID targetUuid;

    @JsonProperty @Getter @Setter
    public CustomPermissionType type;

    @JsonProperty @Getter @Setter
    public Permission permission;

    public enum CustomPermissionType {
        Faction,
        City,
        Player,
    }

    @JsonIgnore
    public IPermission getTarget() {
        return switch (type) {
            case Player -> WarOfSquirrels.instance.getPlayerHandler().get(targetUuid);
            case City -> WarOfSquirrels.instance.getCityHandler().getCity(targetUuid);
            case Faction -> WarOfSquirrels.instance.getFactionHandler().get(targetUuid);
        };
    }
}
