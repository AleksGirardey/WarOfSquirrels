package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import fr.craftandconquest.warofsquirrels.object.scoring.Score;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class FullPlayer implements IPermission {

    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty("DisplayName") @Getter @Setter private String displayName;
    @JsonProperty private Score score = new Score();
    @JsonProperty private UUID cityUuid;
    @JsonProperty @Getter @Setter private Boolean assistant;
    @JsonProperty @Getter @Setter private Boolean resident = false;
    @JsonProperty @Getter @Setter private int balance;
    @JsonProperty @Getter @Setter private boolean fake = false;
    @JsonProperty @Getter @Setter private String lastDimension;
    @JsonProperty @Getter @Setter private int remainingTp;
    @JsonProperty @Getter @Setter private boolean whitelistCityCreator = false;

    /* -- Extra Fields -- */
    @Getter @Setter private int lastChunkX = 10000;
    @Getter @Setter private int lastChunkZ = 10000;
    @Getter @Setter private boolean reincarnation;


    @JsonIgnore @Getter @Setter private boolean adminMode;
    @JsonIgnore @Getter private City city;
    @JsonIgnore public Vector3 lastPosition;
    @JsonIgnore @Getter @Setter private BroadCastTarget chatTarget;

    public void setCity(City city) {
        cityUuid = city != null ? city.getCityUuid() : null;
        this.city = city;
    }

    public void updateDependencies() {
        if (cityUuid != null)
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

    @JsonIgnore
    public ResourceKey<Level> getLastDimensionKey() {
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("minecraft", lastDimension));
    }

    public void update() {
        if (city != null) {
            switch (city.getCityUpgrade().getPalace().getCurrentLevel()) {
                case 2 -> remainingTp = 1;
                case 3, 4 -> remainingTp = 2;
                default -> remainingTp = 0;
            }
        } else {
            remainingTp = 0;
        }
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

    public void reset() {
        setAssistant(false);
        setRemainingTp(0);
        setResident(false);
    }
}
