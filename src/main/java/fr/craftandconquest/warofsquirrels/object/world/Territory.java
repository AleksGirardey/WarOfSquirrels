package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.InfluenceHandler;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Territory {

    @JsonProperty @Getter @Setter private UUID uuid;
    @JsonProperty @Getter @Setter private String name;
    @JsonProperty @Getter @Setter private int posX;
    @JsonProperty @Getter @Setter private int posZ;
    @JsonProperty @Getter private UUID factionUuid;
    @JsonProperty @Getter private UUID fortificationUuid;
    @JsonProperty @Getter @Setter private int dimensionId;

    @JsonIgnore @Getter private Faction faction;
    @JsonIgnore @Getter private IFortification fortification;

    public Territory(String name, int posX, int posZ, Faction faction, IFortification fortification, int dimensionId) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.posX = posX;
        this.posZ = posZ;
        SetFaction(faction);
        SetFortification(fortification);
        this.dimensionId = dimensionId;
    }

    public void SetFortification(IFortification fortification) {
        this.fortification = fortification;
        if (fortification != null)
            this.fortificationUuid = fortification.getUniqueId();
    }

    public void SetFaction(Faction faction) {
        this.faction = faction;
        if (faction != null)
            this.factionUuid = faction.getFactionUuid();
    }

    public int GetInfluenceGenerated() {
        return fortification != null ? fortification.getInfluenceGenerated() : 0;
    }

    public int GetSelfInfluenceGenerated() {
        return fortification != null ? fortification.getSelfInfluenceGenerated() : 0;
    }

    public void SpreadInfluence() {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();
        List<Territory> neighbors = WarOfSquirrels.instance.getTerritoryHandler().getNeighbors(this);

        if (fortification != null) {
            handler.pushInfluence(fortification.getFaction(), this, GetSelfInfluenceGenerated());

            if (faction != null) {
                for (Territory territory : neighbors)
                    handler.pushInfluence(faction, territory, GetInfluenceGenerated());
            }
        }
    }

    @Override
    public String toString() {
        return String.format("[%d;%d] Possédé par %s dans la dimension d'id %d",
                posX,
                posZ,
                faction != null ? faction.getDisplayName() : "personne", dimensionId);
    }
}
