package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Bastion implements IFortification {
    @JsonProperty @Getter @Setter
    private UUID bastionUuid;

    @JsonProperty @Getter @Setter
    private String name;

    @JsonProperty @Getter
    private UUID cityUuid;

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
    public int getSelfInfluenceGenerated() {
        return 0;
    }

    @Override
    public int getInfluenceMax() {
        return 0;
    }

    @Override
    public int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly) {
        return 0;
    }

    @Override
    public int getInfluenceGeneratedDistantNeighbour() {
        return 0;
    }

    @Override
    public int getInfluenceRange() {
        return 0;
    }
}
