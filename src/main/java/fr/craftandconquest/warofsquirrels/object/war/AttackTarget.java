package fr.craftandconquest.warofsquirrels.object.war;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;

public interface AttackTarget {
    @JsonIgnore Faction getFaction();
}
