package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Bastion {
    @JsonProperty @Getter @Setter private UUID bastionUuid;
    @JsonProperty @Getter @Setter private String name;
}
