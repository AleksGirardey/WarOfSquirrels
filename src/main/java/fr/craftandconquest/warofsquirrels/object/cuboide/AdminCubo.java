package fr.craftandconquest.warofsquirrels.object.cuboide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class AdminCubo {
    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty @Getter @Setter private VectorCubo vector;
    @JsonProperty @Getter @Setter private boolean isTeleporter;
    @JsonProperty @Getter @Setter private Vector3 respawnPoint;
    @JsonProperty @Getter @Setter private String respawnDimension;
    @JsonProperty @Getter @Setter private UUID linkedPortal;

    @JsonIgnore
    public ResourceKey<Level> getDimensionKey() {
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation("minecraft", respawnDimension));
    }

    @Override
    public String toString() {
        return String.format("[Cubo] Admin Cubo set as '%s'.", name);
    }
}
