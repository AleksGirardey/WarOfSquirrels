package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Player implements IPermission {

    @JsonIgnore @Getter @Setter private PlayerEntity playerEntity;

    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty("DisplayName") @Getter @Setter private String displayName;
    @JsonProperty private int score;
    @JsonProperty private int cityId;
    @JsonProperty @Getter @Setter private Boolean assistant;
    @JsonProperty private Boolean resident;
    @JsonProperty @Getter @Setter private int balance;

    /* -- Extra Fields -- */
    private int             lastChunkX = 10000;
    private int             lastChunkZ = 10000;
    @Getter @Setter private boolean         reincarnation;
    private boolean         adminMode;
    private long            lastClick;

    @JsonIgnore @Getter private City city;

    public Vec3d lastPosition;
    public DimensionType lastDimension;

    public void setCity(City city) {
        cityId = city != null ? city.getCityId() : -1;
        this.city = city;
    }

    public void updateDependencies() {
        city = WarOfSquirrels.instance.getCityHandler().getCity(cityId);
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.PLAYER;
    }
}
