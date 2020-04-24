package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.city.City;
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
public class Player {

    /* -- DB Fields -- */
//    private User            user;
    @JsonIgnore @Getter @Setter private PlayerEntity playerEntity;
    @JsonProperty("DisplayName") @Getter @Setter private String displayName;
    private int             score;
    private int             cityId;
    @Getter @Setter private Boolean         assistant;
    private Boolean         resident;
    private int             balance;

    /* -- Extra Fields -- */
    private int             lastChunkX = 10000;
    private int             lastChunkZ = 10000;
    private boolean         reincarnation;
    private boolean         adminMode;
    private long            lastClick;

    @JsonIgnore @Getter private City city;

    public Vec3d lastPosition;
    public DimensionType lastDimension;

    public void setCity(City city) {
        cityId = city != null ? city.getCityId() : -1;
        this.city = city;
    }

    public UUID getUUID() {
        return playerEntity.getUniqueID();
    }
}
