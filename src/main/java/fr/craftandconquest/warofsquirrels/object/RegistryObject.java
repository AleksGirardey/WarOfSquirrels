package fr.craftandconquest.warofsquirrels.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public abstract class RegistryObject {
    @JsonProperty @Getter @Setter protected UUID uuid;
    @JsonProperty @Getter @Setter protected String displayName;

    public abstract void updateDependencies();
}
