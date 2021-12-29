package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.broadcast.BroadCastTarget;
import fr.craftandconquest.warofsquirrels.handler.broadcast.IChannelTarget;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
public class Faction implements IPermission, IChannelTarget {

    @JsonProperty
    @Getter
    private UUID factionUuid;
    @JsonProperty
    @Getter
    @Setter
    private UUID capitalUuid;
    @JsonProperty
    @Getter
    @Setter
    private String displayName;
    @JsonIgnore
    @Getter
    private City capital;
    @JsonIgnore
    @Getter
    private Map<String, City> cities;

    @JsonIgnore
    @Getter
    @Setter
    private Map<IPermission, Permission> customPermission = new HashMap<>();
    @Getter
    @Setter
    private Map<PermissionRelation, Permission> defaultPermission;
    @Getter
    @Setter
    private List<CustomPermission> customPermissionList = new ArrayList<>();

    public Faction(String displayName, City capital) {
        this.factionUuid = UUID.randomUUID();
        this.displayName = displayName;
        this.capital = capital;
        this.capitalUuid = capital.getCityUuid();
    }

    public void SetCapital(City city) {
        this.capital = city;
        this.capitalUuid = city.getCityUuid();
    }

    private void Populate() {
        List<City> cities = WarOfSquirrels.instance.getCityHandler().getCities(this);

        for (City city : cities) {
            this.cities.put(city.displayName, city);
        }
    }

    public void updateDependencies() {
        this.capital = WarOfSquirrels.instance.getCityHandler().getCity(this.capitalUuid);
        Populate();

        for (CustomPermission permission : customPermissionList) {
            IPermission target = permission.getTarget();

            if (target == null) continue;

            customPermission.put(target, permission.permission);
        }
    }

    @Override
    public BroadCastTarget getBroadCastTarget() {
        return BroadCastTarget.FACTION;
    }

    @Override
    public UUID getUuid() {
        return factionUuid;
    }

    @Override
    public PermissionTarget getPermissionTarget() {
        return PermissionTarget.FACTION;
    }

    @Override
    public String getPermissionDisplayName() {
        return "F:" + getDisplayName();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", displayName, capital.displayName);
    }
}
