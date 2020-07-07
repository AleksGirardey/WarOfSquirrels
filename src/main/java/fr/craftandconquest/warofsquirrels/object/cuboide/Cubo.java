package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.PlayerHandler;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Cubo {
    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty private UUID cityUuid;
    @JsonProperty private UUID parentUuid;
    @JsonProperty private UUID ownerUuid;
    @JsonProperty @Getter private Permission permissionIn;
    @JsonProperty @Getter private Permission permissionOut;
    //@JsonProperty private UUID permissionInUuid;
    //@JsonProperty private UUID permissionOutUuid;
    @JsonProperty private List<UUID> inListUuid;
//    @JsonProperty private UUID loanUuid;
    @JsonProperty @Getter @Setter private int priority;
    @JsonProperty @Getter @Setter private VectorCubo vector;

    @JsonIgnore @Getter private City city;
    @JsonIgnore @Getter private Cubo parent;
    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private List<Player> inList;
//    @JsonIgnore @Getter private Loan loan;

    public void AddPlayerInList(Player player) {
        inList.add(player);
        inListUuid.add(player.getUuid());
    }

    private void UpdateDependencies() {
        WarOfSquirrels wos = WarOfSquirrels.instance;
        PlayerHandler playerHandler = wos.getPlayerHandler();

        this.city = wos.getCityHandler().getCity(cityUuid);
        this.parent = wos.getCuboHandler().getCubo(parentUuid);
        this.owner = playerHandler.get(ownerUuid);
        this.inList = new ArrayList<>();

        for (UUID uuid : inListUuid)
            inList.add(playerHandler.get(uuid));
    }

    @Override
    public String toString() {
        return String.format("[Cubo][New] Cubo set as '%s' with parent '%s' owned by '%s'.",
                name, parent == null ? "NO_PARENT": parent.name, owner.getDisplayName());
    }
}
