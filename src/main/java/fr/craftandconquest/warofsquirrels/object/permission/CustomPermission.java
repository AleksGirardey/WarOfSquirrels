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
    public PermissionTarget type;

    @JsonProperty @Getter @Setter
    public Permission permission;

    @JsonIgnore
    public IPermission getTarget() {
        return switch (type) {
            case PLAYER -> WarOfSquirrels.instance.getPlayerHandler().get(targetUuid);
            case CITY -> WarOfSquirrels.instance.getCityHandler().get(targetUuid);
            case FACTION -> WarOfSquirrels.instance.getFactionHandler().get(targetUuid);
            case GUILD -> WarOfSquirrels.instance.getGuildHandler().get(targetUuid);
        };
    }
}
