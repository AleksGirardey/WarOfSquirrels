package fr.craftandconquest.warofsquirrels.object.faction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.object.faction.city.City;
import fr.craftandconquest.warofsquirrels.object.world.Territory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
public class Influence {
    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private UUID factionUuid;
    @JsonProperty @Getter @Setter private UUID cityUuid;
    @JsonProperty @Getter @Setter private UUID territoryUuid;
    @JsonProperty @Getter @Setter int value;

    @JsonIgnore @Getter private City city;
    @JsonIgnore @Getter private Faction faction;
    @JsonIgnore @Getter private Territory territory;

    @JsonCreator
    public Influence(
            @JsonProperty("uuid") UUID _uuid,
            @JsonProperty("factionUuid") UUID _factionUuid,
            @JsonProperty("cityUuid") UUID _cityUuid,
            @JsonProperty("territoryUuid") UUID _territoryUuid,
            @JsonProperty("value") int _value) {
        uuid = _uuid;
        factionUuid = _factionUuid;
        cityUuid = _cityUuid;
        territoryUuid = _territoryUuid;
        value = _value;
    }

    public Influence(City city, Territory territory) {
        this.uuid = UUID.randomUUID();
        this.value = 0;
        SetCity(city);
        SetTerritory(territory);
    }

    public Influence(Faction faction, Territory territory) {
        this.uuid = UUID.randomUUID();
        this.value = 0;
        SetFaction(faction);
        SetTerritory(territory);
    }

    public void SetCity(City city) {
        this.city = city;
        if (city != null)
            this.cityUuid = city.getUuid();
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
        this.value = Math.min(value + influence, territory.getInfluenceMax());
    }

    public void SubInfluence(int influence) {
        this.value = Math.max(value - influence, 0);
    }

    public void updateDependencies() {
        if (cityUuid != null) city = WarOfSquirrels.instance.getCityHandler().getCity(cityUuid);
        if (factionUuid != null) faction = WarOfSquirrels.instance.getFactionHandler().get(factionUuid);
        if (territoryUuid != null) territory = WarOfSquirrels.instance.getTerritoryHandler().get(territoryUuid);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        Influence influence = (Influence) obj;

        return influence.getUuid().equals(this.uuid);
    }

    @Override
    public String toString() {
        return String.format("[Influence] %s influence le territoire %s à hauteur de %d influence.",
                faction != null ? faction.getDisplayName() : city != null ? city.getDisplayName() : "none", territory.getName(), value);
    }
}
