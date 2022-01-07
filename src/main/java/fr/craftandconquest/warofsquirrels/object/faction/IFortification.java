package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;

import java.util.UUID;

public interface IFortification {
    enum FortificationType {
        CITY,
        BASTION
    }

    @JsonIgnore
    UUID getUniqueId();

    @JsonIgnore
    Faction getFaction();

    @JsonIgnore
    City getRelatedCity();

    @JsonIgnore
    FortificationType getFortificationType();

    @JsonIgnore
    int getSelfInfluenceGenerated();

    @JsonIgnore
    int getInfluenceMax();

    @JsonIgnore
    int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly);

    @JsonIgnore
    int getInfluenceGeneratedDistantNeighbour();

    @JsonIgnore
    int getInfluenceRange();
}
