package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.InfluenceHandler;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class Territory {

    @JsonProperty
    @Getter
    @Setter
    private UUID uuid;
    @JsonProperty
    @Getter
    @Setter
    private String name;
    @JsonProperty
    @Getter
    @Setter
    private int posX;
    @JsonProperty
    @Getter
    @Setter
    private int posZ;
    @JsonProperty
    @Getter
    private UUID factionUuid;
    @JsonProperty
    @Getter
    private UUID fortificationUuid;
    @JsonProperty
    @Getter
    @Setter
    private String biome;
    @JsonProperty
    @Getter
    private Map<String, Integer> biomeMap;

    @JsonIgnore
    @Getter
    private Faction faction;
    @JsonIgnore
    @Getter
    private IFortification fortification;

    public Territory(String name, int posX, int posZ, Faction faction, IFortification fortification) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.posX = posX;
        this.posZ = posZ;
        SetFaction(faction);
        SetFortification(fortification);
        this.biome = "NONE";
        biomeMap = new HashMap<>();

        SetBiomeMap();
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

    public void SpreadInfluence() {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();

        if (fortification != null) {
            if (faction == null) {
                handler.pushInfluence(fortification, this, fortification.getSelfInfluenceGenerated());
                return;
            }

            SpreadSelfInfluence();
            SpreadCloseInfluence(true);
            if (fortification.getFortificationType() == IFortification.FortificationType.BASTION)
                SpreadCloseInfluence(false);
            SpreadDistantInfluence();
        }
    }

    public void SpreadSelfInfluence() {
        WarOfSquirrels.instance.getInfluenceHandler().pushInfluence(fortification, this, fortification.getSelfInfluenceGenerated());
    }

    public void SpreadCloseInfluence(boolean neutralOnly) {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();
        List<Territory> neighbors = WarOfSquirrels.instance.getTerritoryHandler().getNeighbors(this);

        for (Territory territory : neighbors) {
            if (neutralOnly && territory.getFaction() != null) continue;

            handler.pushInfluence(fortification, territory, fortification.getInfluenceGeneratedCloseNeighbour(neutralOnly));
        }
    }

    public void SpreadDistantInfluence() {
        InfluenceHandler handler = WarOfSquirrels.instance.getInfluenceHandler();
        List<Territory> neighbors = WarOfSquirrels.instance.getTerritoryHandler().getNeighbors(this, fortification.getInfluenceRange());

        for (Territory territory : neighbors) {
            if (territory.getFaction() == null)
                handler.pushInfluence(fortification, territory, fortification.getInfluenceGeneratedDistantNeighbour());
        }
    }

    @JsonIgnore
    public int getInfluenceMax() {
        return fortification.getInfluenceMax(); // Ajouter influence du biome du territoire
    }

    private void SetBiomeMap() {
        biomeMap = new HashMap<>();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posXMin = posX * territorySize;
        int posXMax = posXMin + territorySize;
        int posZMin = posZ * territorySize;
        int posZMax = posZMin + territorySize;

        Level level = WarOfSquirrels.server.getLevel(Level.OVERWORLD);

        if (level == null) return;

        BiomeManager biomeManager = level.getBiomeManager();

        for (int x = posXMin; x < posXMax; ) {
            for (int z = posZMin; z < posZMax; ) {
                ChunkPos chunkPos = Utils.WorldToChunkPos(x, z);

                Biome.BiomeCategory category = biomeManager.getPrimaryBiomeAtChunk(chunkPos).getBiomeCategory();

                if (!biomeMap.containsKey(category.getSerializedName()))
                    biomeMap.put(category.getSerializedName(), 0);
                biomeMap.compute(category.getSerializedName(), (k, v) -> v += 1);
                z += 16;
            }
            x += 16;
        }

        String mainBiome = "NONE";
        int count = 0;

        for (Map.Entry<String, Integer> pair : biomeMap.entrySet()) {
            if (pair.getValue() > count)
                mainBiome = pair.getKey();
        }

        biome = mainBiome;
    }

    @Override
    public String toString() {
        return String.format("[%d;%d] Possédé par %s dans la dimension d'id %s de type %s",
                posX,
                posZ,
                faction != null ? faction.getDisplayName() : "personne", "Overworld", biome);
    }
}
