package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Player implements IPermission {

    @JsonIgnore @Getter @Setter private PlayerEntity playerEntity;

    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty("DisplayName") @Getter @Setter private String displayName;
    @JsonProperty private int score;
    @JsonProperty private UUID cityUuid;
    @JsonProperty @Getter @Setter private Boolean assistant;
    @JsonProperty @Getter @Setter private Boolean resident = false;
    @JsonProperty @Getter @Setter private int balance;

    /* -- Extra Fields -- */
    @Getter @Setter private int             lastChunkX = 10000;
    @Getter @Setter private int             lastChunkZ = 10000;
    @Getter @Setter private boolean         reincarnation;
    @JsonProperty @Getter @Setter private boolean         adminMode;
    private long            lastClick;

    @JsonIgnore @Getter private City city;

    @JsonIgnore public Vector3 lastPosition;
    @JsonIgnore public DimensionType lastDimension;

    public void setCity(City city) {
        cityUuid = city != null ? city.getCityUuid() : null;
        this.city = city;
    }

    public void updateDependencies() {
        city = WarOfSquirrels.instance.getCityHandler().getCity(cityUuid);
    }

    @Override @JsonIgnore
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.PLAYER;
    }

    @JsonIgnore
    public boolean isOnline() { return (WarOfSquirrels.server.getPlayerList().getPlayerByUUID(uuid) != null); }
}
