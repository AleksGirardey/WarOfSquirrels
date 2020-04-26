package fr.craftandconquest.warofsquirrels.object.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionRelation;
import fr.craftandconquest.warofsquirrels.object.permission.PermissionTarget;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class City implements IPermission {
    @Getter @Setter
    private int cityId;
    public String displayName;
    public String tag;
    public UUID ownerUUID;

    @Getter private CityRank   rank;

    @Getter @Setter private Map<IPermission, Permission> customPermission;
    @Getter @Setter private Map<PermissionRelation, Permission> defaultPermission;

    private int         balance;

    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private List<Player> citizens = new ArrayList<>();

    public boolean addCitizen(Player player) {
        if (citizens.contains(player)) return false;

        citizens.add(player);
        return true;
    }

    public boolean removeCitizen(Player player) {
        if (!citizens.contains(player)) return false;

        return citizens.remove(player);
    }

    public void SetOwner(Player owner) {
        ownerUUID = owner != null ? owner.getUUID() : null;
        this.owner = owner;
    }

    public void SetRank(int rank) {
        this.rank = WarOfSquirrels.instance.getConfig().getCityRankMap().get(rank);
    }

    public List<String> getCitizensAsList() {
        List<String> res = new ArrayList<>();

        for (Player player : citizens) {
            res.add(player.getDisplayName());
        }
        return res;
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.CITY;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", tag, displayName);
    }
}
