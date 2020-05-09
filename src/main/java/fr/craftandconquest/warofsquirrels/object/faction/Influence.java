package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
public class Influence {
    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter private UUID factionUuid;
    @JsonProperty @Getter private UUID territoryUuid;
    @JsonProperty @Getter int value;

    @JsonIgnore @Getter private Faction faction;
    @JsonIgnore @Getter private Territory territory;

    public Influence() {
        this.uuid = UUID.randomUUID();
        this.value = 0;
    }

    public Influence(Faction faction, Territory territory) {
        super();
        SetFaction(faction);
        SetTerritory(territory);
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        this.factionUuid = faction != null
                ? faction.getFactionUuid()
                : null;
    }

    public void SetTerritory(Territory territory) {
        this.territory = territory;
        this.territoryUuid = territory != null
                ? territory.getUuid()
                : null;
    }

    public void AddInfluence(int influence) {
        this.value = Math.min(value + influence, WarOfSquirrels.instance.getConfig().getInfluenceMax());
    }

    public void SubInfluence(int influence) {
        this.value = Math.max(value - influence, 0);
    }

    @Override
    public String toString() {
        return String.format("[Influence] %s influence le territoire %s à hauteur de %d influence.",
                faction.getDisplayName(), territory.getName(), value);
    }
}
