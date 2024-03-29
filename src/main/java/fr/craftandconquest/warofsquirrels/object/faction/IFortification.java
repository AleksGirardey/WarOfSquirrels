package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.FullPlayer;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.scoring.IScoreUpdater;
import fr.craftandconquest.warofsquirrels.utils.Vector2;
import fr.craftandconquest.warofsquirrels.utils.Vector3;

import java.util.UUID;

public interface IFortification extends IScoreUpdater {
    enum FortificationType {
        CITY,
        BASTION
    }

    @JsonIgnore Vector2 getTerritoryPosition();

    @JsonIgnore String getDisplayName();

    @JsonIgnore UUID getUuid();

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

    @JsonIgnore void displayInfo(FullPlayer player);
}
