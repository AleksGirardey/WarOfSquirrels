package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class FullPlayer implements IPermission {

//    @JsonIgnore
//    @Getter
//    @Setter
//    private Player playerEntity;

    @JsonProperty
    @Getter
    @Setter
    private UUID uuid;
    @JsonProperty("DisplayName")
    @Getter
    @Setter
    private String displayName;
    @JsonProperty
    private int score;
    @JsonProperty
    private UUID cityUuid;
    @JsonProperty
    @Getter
    @Setter
    private Boolean assistant;
    @JsonProperty
    @Getter
    @Setter
    private Boolean resident = false;
    @JsonProperty
    @Getter
    @Setter
    private int balance;

    /* -- Extra Fields -- */
    @Getter
    @Setter
    private int lastChunkX = 10000;
    @Getter
    @Setter
    private int lastChunkZ = 10000;
    @Getter
    @Setter
    private boolean reincarnation;
    @JsonProperty
    @Getter
    @Setter
    private boolean adminMode;
    private long lastClick;

    @JsonIgnore
    @Getter
    private City city;

    @JsonIgnore
    public Vector3 lastPosition;
    @JsonIgnore
    public ResourceKey<Level> lastDimension;

    @JsonIgnore
    @Getter @Setter
    private BroadCastTarget chatTarget;

    public void setCity(City city) {
        cityUuid = city != null ? city.getCityUuid() : null;
        this.city = city;
    }

    public void updateDependencies() {
        WarOfSquirrels.instance.getCityHandler().getCity(cityUuid).register(this);
    }

    @JsonIgnore
    public Player getPlayerEntity() { return WarOfSquirrels.server.getPlayerList().getPlayer(this.uuid); }

    @Override
    @JsonIgnore
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.PLAYER;
    }

    @Override
    public String getPermissionDisplayName() {
        return "P:" + getDisplayName();
    }

    @JsonIgnore
    public boolean isOnline() {
        return (WarOfSquirrels.server.getPlayerList().getPlayer(uuid) != null);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        final FullPlayer target = (FullPlayer) obj;

        return target.getUuid().equals(this.uuid);
    }

    public boolean sendMessage(MutableComponent message) {
        Player player = getPlayerEntity();

        if (player != null) {
            player.sendMessage(message, Util.NIL_UUID);
            return true;
        }

        return false;
    }
}
