package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class Bastion implements IFortification {
    @JsonProperty
    @Getter
    @Setter
    private UUID bastionUuid;
    @JsonProperty
    @Getter
    @Setter
    private String name;
    @JsonProperty
    @Getter
    private UUID factionUuid;

    @JsonIgnore
    @Getter
    private Faction faction;

    public void SetFaction(Faction faction) {
        this.faction = faction;
        if (faction != null)
            this.factionUuid = faction.getFactionUuid();
    }

    @Override
    public UUID getUniqueId() {
        return bastionUuid;
    }
}
