package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.IPermission;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
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
public class Cubo {
    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty private UUID cityUuid;
    @JsonProperty private UUID parentUuid;
    @JsonProperty private UUID ownerUuid;
    @JsonProperty @Getter @Setter private Permission permissionIn;
    @JsonProperty @Getter @Setter private Permission permissionOut;
    //@JsonProperty private UUID permissionInUuid;
    //@JsonProperty private UUID permissionOutUuid;
    @JsonProperty private List<UUID> inListUuid;
//    @JsonProperty private UUID loanUuid;
    @JsonProperty private Map<UUID, Permission> customInListUuid;
    @JsonProperty @Getter @Setter private int priority;
    @JsonProperty @Getter @Setter private VectorCubo vector;

    @JsonIgnore @Getter private City city;
    @JsonIgnore @Getter private Cubo parent;
    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private List<Player> inList;
    @JsonIgnore private Map<Player, Permission> customInList;
//    @JsonIgnore @Getter private Loan loan;

    public void AddPlayerInList(Player player) {
        inList.add(player);
        inListUuid.add(player.getUuid());
    }

    public void AddPlayerCustomPermission(Player player, Permission permission) {
        customInList.put(player, permission);
        customInListUuid.put(player.getUuid(), permission);
    }

    public void UpdateDependencies() {
        WarOfSquirrels wos = WarOfSquirrels.instance;
        PlayerHandler playerHandler = wos.getPlayerHandler();

        this.city = wos.getCityHandler().getCity(cityUuid);
        this.parent = wos.getCuboHandler().getCubo(parentUuid);
        this.owner = playerHandler.get(ownerUuid);
        this.inList = new ArrayList<>();

        for (UUID uuid : inListUuid)
            inList.add(playerHandler.get(uuid));

        customInListUuid.forEach((k, v) -> customInList.put(playerHandler.get(k), v));
    }

    public void setCity(City city) {
        this.city = city;
        this.cityUuid = city.getUniqueId();
        WarOfSquirrels.instance.getCuboHandler().Save();
    }

    public void setParent(Cubo parent) {
        this.parent = parent;
        this.parentUuid = parent.getUuid();
        WarOfSquirrels.instance.getCuboHandler().Save();
    }

    public void setOwner(Player owner) {
        this.owner = owner;
        this.ownerUuid = owner.getUuid();
        WarOfSquirrels.instance.getCuboHandler().Save();
    }

    public void SpreadPermissionDelete(IPermission target) {
        if (target instanceof Player) {
            customInList.remove(target);
            customInListUuid.remove(((Player) target).getUuid());
        }
    }

    @Override
    public String toString() {
        return String.format("[Cubo][New] Cubo set as '%s' with parent '%s' owned by '%s'.",
                name, parent == null ? "NO_PARENT": parent.name, owner.getDisplayName());
    }
}
