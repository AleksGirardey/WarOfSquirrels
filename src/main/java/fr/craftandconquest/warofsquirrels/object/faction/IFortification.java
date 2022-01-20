package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.utils.Vector3;

import java.util.UUID;

public interface IFortification {
    enum FortificationType {
        CITY,
        BASTION
    }

    @JsonIgnore String getDisplayName();

    @JsonIgnore UUID getUniqueId();

    @JsonIgnore Faction getFaction();

    @JsonIgnore City getRelatedCity();

    @JsonIgnore FortificationType getFortificationType();

    @JsonIgnore int getSelfInfluenceGenerated(boolean gotAttacked, boolean gotDefeated);

    @JsonIgnore int getInfluenceGeneratedCloseNeighbour(boolean neutralOnly, boolean gotAttacked, boolean gotDefeated);

    @JsonIgnore int getInfluenceGeneratedDistantNeighbour(boolean gotAttacked, boolean gotDefeated);

    @JsonIgnore int getInfluenceDamage(boolean gotAttacked, boolean gotDefeated);

    @JsonIgnore int getInfluenceMax();

    @JsonIgnore int getInfluenceRange();

    @JsonIgnore Vector3 getSpawn();

    @JsonIgnore boolean isProtected();

    @JsonIgnore int getMaxChunk();

    @JsonIgnore int getLinkedChunkSize();
}
