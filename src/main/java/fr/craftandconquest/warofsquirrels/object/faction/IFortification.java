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
    default int getSelfInfluenceGenerated() {
        return 100;
    }

    @JsonIgnore
    default int getInfluenceGenerated() {
        return 100;
    }
}
