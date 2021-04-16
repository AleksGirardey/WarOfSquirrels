package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import fr.craftandconquest.warofsquirrels.handler.InfluenceHandler;
import fr.craftandconquest.warofsquirrels.object.faction.Faction;
import fr.craftandconquest.warofsquirrels.object.faction.IFortification;
import fr.craftandconquest.warofsquirrels.utils.Utils;
import fr.craftandconquest.warofsquirrels.utils.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @JsonProperty @Getter @Setter private String biome;

    @JsonIgnore @Getter private Map<Integer, Pair<String, Integer>> biomeMap;
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

        //SetBiomeMap();
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

    private void SetBiomeMap() {
        biomeMap = new HashMap<>();
        int territorySize = WarOfSquirrels.instance.getConfig().getTerritorySize();
        int posXMin = posX * territorySize;
        int posXMax = posXMin + territorySize;
        int posZMin = posX * territorySize;
        int posZMax = posXMin + territorySize;

        ForgeRegistry<Biome> registry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;

        for (int x = posXMin; x < posXMax;) {
            for (int z = posZMin; z < posZMax;) {
                Pair<Integer, Integer> chunkPos = Utils.WorldToChunkCoordinates(x, z);
                BiomeContainer biomes = WarOfSquirrels.server.getWorld(DimensionType.OVERWORLD)
                        .getChunk(chunkPos.getKey(), chunkPos.getValue())
                        .func_225549_i_();
                if (biomes != null) {
                    for (int biomeId : biomes.func_227055_a_()) {
                        if (!biomeMap.containsKey(biomeId))
                            biomeMap.put(biomeId, new Pair<>(registry.getValue(biomeId).getTranslationKey(), 0));
                        biomeMap.get(biomeId).setValue(biomeMap.get(biomeId).getValue() + 1);
                    }
                }
                z += 16;
            }
            x += 16;
        }

        String mainBiome = "NONE";
        int count = 0;

        for (Pair<String, Integer> pair : biomeMap.values()) {
            if (pair.getValue() > count)
                mainBiome = pair.getKey();
        }

        biome = mainBiome;
    }

    @Override
    public String toString() {
        return String.format("[%d;%d] Possédé par %s dans la dimension d'id %d de type %s",
                posX,
                posZ,
                faction != null ? faction.getDisplayName() : "personne", dimensionId, biome);
    }
}
