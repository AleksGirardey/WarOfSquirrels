package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.Player;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.permission.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JsonProperty private UUID permissionInUuid;
    @JsonProperty private UUID permissionOutUuid;
    @JsonProperty private UUID[] inListUuid;
    @JsonProperty private UUID loanUuid;
    @JsonProperty @Getter @Setter private int priority;
    @JsonProperty @Getter @Setter private VectorCubo vector;

    @JsonIgnore @Getter private City city;
    @JsonIgnore @Getter private Cubo parent;
    @JsonIgnore @Getter private Player owner;
    @JsonIgnore @Getter private Permission permissionIn;
    @JsonIgnore @Getter private Permission permissionOut;
    @JsonIgnore @Getter private List<Player> inList;
//    @JsonIgnore @Getter private Loan loan;
}
