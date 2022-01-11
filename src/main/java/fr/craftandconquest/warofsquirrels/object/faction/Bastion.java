package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Chunk;
import fr.craftandconquest.warofsquirrels.utils.Vector3;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.UUID;

public class Bastion implements IFortification {
    @JsonProperty @Getter @Setter
    private UUID bastionUuid;

    @JsonProperty @Getter @Setter
    private String name;

    @JsonProperty @Getter
    private UUID cityUuid;

    @JsonProperty @Setter
    private boolean isProtected;

    @JsonIgnore @Getter
    private City city;


    public void SetCity(City city) {
        this.city = city;
        if (city != null)
            this.cityUuid = city.getCityUuid();
    }

    @Override
    public Faction getFaction() {
        return this.city.getFaction();
    }

    @Override
    public UUID getUniqueId() {
        return bastionUuid;
    }

    @Override
    public City getRelatedCity() {
        return city;
    }

    @Override
    public FortificationType getFortificationType() {
        return null;
    }

    @Override
    public int getSelfInfluenceGenerated(boolean gotAttacked) {
        int baseInfluence = gotAttacked ? 0 : 100;
        return baseInfluence;
    }

    @Override
    public int getInfluenceMax() {
        return 4000;
    }

    @Override
    public int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly, boolean gotAttacked) {
        int baseInfluence = gotAttacked ? 0 : 50;
        return baseInfluence;
    }

    @Override
    public int getInfluenceGeneratedDistantNeighbour(boolean gotAttacked) {
        return 0;
    }

    @Override
    public int getInfluenceRange() {
        return 0;
    }

    @JsonIgnore
    public Chunk getHomeBlock() {
        return WarOfSquirrels.instance.getChunkHandler().getHomeBlock(this);
    }

    @JsonIgnore @Override
    public Vector3 getSpawn() {
        return getHomeBlock().getRespawnPoint();
    }

    @JsonIgnore @Override
    public boolean isProtected() { return isProtected; }

    @Override
    public int getMaxChunk() {
        return 0; // CHANGE ME
    }

    @Override
    public int getLinkedChunkSize() {
        return WarOfSquirrels.instance.getChunkHandler().getChunks(this).size();
    }
}
