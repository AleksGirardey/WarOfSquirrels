package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.craftandconquest.warofsquirrels.utils.ChatText;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class TerritoryBiome {
    @JsonIgnore public static int riverChunkCount;
    @JsonIgnore private static final Map<ResourceKey<Biome>, EBiomeType> biomeMapping = new HashMap<>();

    static {
        /* Plains */
        biomeMapping.put(Biomes.PLAINS, EBiomeType.Plains);
        biomeMapping.put(Biomes.SUNFLOWER_PLAINS, EBiomeType.Plains);
        biomeMapping.put(Biomes.SNOWY_PLAINS, EBiomeType.Plains);
        biomeMapping.put(Biomes.SAVANNA, EBiomeType.Plains);
        biomeMapping.put(Biomes.DESERT, EBiomeType.Plains);
        biomeMapping.put(Biomes.MUSHROOM_FIELDS, EBiomeType.Plains);
        biomeMapping.put(Biomes.MEADOW, EBiomeType.Plains);

        /* Mountain */
        biomeMapping.put(Biomes.BADLANDS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.ERODED_BADLANDS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.WOODED_BADLANDS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.ICE_SPIKES, EBiomeType.Mountains);
        biomeMapping.put(Biomes.SAVANNA_PLATEAU, EBiomeType.Mountains);
        biomeMapping.put(Biomes.WINDSWEPT_HILLS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.WINDSWEPT_FOREST, EBiomeType.Mountains);
        biomeMapping.put(Biomes.WINDSWEPT_SAVANNA, EBiomeType.Mountains);
        biomeMapping.put(Biomes.SNOWY_SLOPES, EBiomeType.Mountains);
        biomeMapping.put(Biomes.FROZEN_PEAKS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.JAGGED_PEAKS, EBiomeType.Mountains);
        biomeMapping.put(Biomes.STONY_PEAKS, EBiomeType.Mountains);

        /* Forest */
        biomeMapping.put(Biomes.JUNGLE, EBiomeType.Forest);
        biomeMapping.put(Biomes.SPARSE_JUNGLE, EBiomeType.Forest);
        biomeMapping.put(Biomes.BAMBOO_JUNGLE, EBiomeType.Forest);
        biomeMapping.put(Biomes.FOREST, EBiomeType.Forest);
        biomeMapping.put(Biomes.FLOWER_FOREST, EBiomeType.Forest);
        biomeMapping.put(Biomes.BIRCH_FOREST, EBiomeType.Forest);
        biomeMapping.put(Biomes.DARK_FOREST, EBiomeType.Forest);
        biomeMapping.put(Biomes.OLD_GROWTH_BIRCH_FOREST, EBiomeType.Forest);
        biomeMapping.put(Biomes.OLD_GROWTH_PINE_TAIGA, EBiomeType.Forest);
        biomeMapping.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, EBiomeType.Forest);
        biomeMapping.put(Biomes.TAIGA, EBiomeType.Forest);
        biomeMapping.put(Biomes.SNOWY_TAIGA, EBiomeType.Forest);
        biomeMapping.put(Biomes.GROVE, EBiomeType.Forest);

        /* Ocean */
        biomeMapping.put(Biomes.BEACH, EBiomeType.Ocean);
        biomeMapping.put(Biomes.SNOWY_BEACH, EBiomeType.Ocean);
        biomeMapping.put(Biomes.STONY_SHORE, EBiomeType.Ocean);
        biomeMapping.put(Biomes.OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.DEEP_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.COLD_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.WARM_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.FROZEN_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.LUKEWARM_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.DEEP_LUKEWARM_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.DEEP_COLD_OCEAN, EBiomeType.Ocean);
        biomeMapping.put(Biomes.DEEP_FROZEN_OCEAN, EBiomeType.Ocean);

        /* Swamp */
        biomeMapping.put(Biomes.SWAMP, EBiomeType.Swamp);
        biomeMapping.put(Biomes.MANGROVE_SWAMP, EBiomeType.Swamp);

        /* River */
        biomeMapping.put(Biomes.RIVER, EBiomeType.River);
        biomeMapping.put(Biomes.FROZEN_RIVER, EBiomeType.River);

        /* None */
    }

    enum EBiomeType {
        Forest,
        Mountains,
        Ocean,
        Plains,
        River,
        Swamp,
        None,
    }
    @JsonIgnore @Getter private List<EBiomeType> biomes;

    @JsonProperty @Getter private List<String> serializedBiomes;
    @JsonProperty @Getter private boolean hasRiver = false;
    @JsonProperty("isRiverComplete") private boolean isRiverComplete = false;

    @JsonIgnore public double sum;

    @JsonCreator
    public TerritoryBiome(@JsonProperty("serializedBiomes") List<String> _biomes, @JsonProperty("hasRiver") boolean _hasRiver, @JsonProperty("isRiverComplete") boolean _isRiverComplete) {
        biomes = new ArrayList<>();
        serializedBiomes = new ArrayList<>(_biomes);
        hasRiver = _hasRiver;
        isRiverComplete = _isRiverComplete;

        Populate();
    }

    public TerritoryBiome(Map<ResourceKey<Biome>, Integer> biomeMap) {
        biomes = new ArrayList<>();
        serializedBiomes = new ArrayList<>();

        Map<EBiomeType, Integer> groupedMap = new HashMap<>();

        int max = biomeMap.values().stream().mapToInt(value -> value).sum();
        int minValueToBeComplete = 132;

        for (Map.Entry<ResourceKey<Biome>, Integer> biomeEntry : biomeMap.entrySet()) {
            EBiomeType type = biomeMapping.getOrDefault(biomeEntry.getKey(), EBiomeType.None);

            groupedMap.compute(type, (k, v) -> v = (v == null ? biomeEntry.getValue() : v + biomeEntry.getValue()));
        }

        if (groupedMap.containsKey(EBiomeType.River)) {
            sum = groupedMap.get(EBiomeType.River) - 91;
            sum *= sum;

            TerritoryBiome.riverChunkCount += groupedMap.get(EBiomeType.River);
            hasRiver = groupedMap.get(EBiomeType.River) >= 80;
            isRiverComplete = groupedMap.get(EBiomeType.River) >= minValueToBeComplete;
            groupedMap.remove(EBiomeType.River);
        }

        for (Map.Entry<EBiomeType, Integer> entry : groupedMap.entrySet()) {
            if (entry.getKey().equals(EBiomeType.None)) return;

            int percent = entry.getValue() * 100 / max;

            if (percent >= 33)
                addBiome(entry.getKey());
        }

//        groupedMap.forEach((k, v) -> v = ((v * 100) / max));
//        groupedMap.entrySet().stream()
//                .filter(e -> e.getValue() >= 33 && e.getKey() != EBiomeType.None)
//                .forEach(e -> addBiome(e.getKey()));
    }

    private void addBiome(EBiomeType type) {
        biomes.add(type);
        serializedBiomes.add(type.name());
    }

    @JsonIgnore
    public boolean isCompleteTrait() { return biomes.size() == 1; }

    public float ratioBonusInfluenceOnMe() {
        if (biomes.contains(EBiomeType.Plains)) {
            return isCompleteTrait() ? 1f : 0.5f;
        }
        return 0f;
    }

    public float ratioBonusInfluenceFromMe() {
        if (biomes.contains(EBiomeType.Mountains)) {
            return isCompleteTrait() ? -0.75f : -0.5f;
        }
        return 0f;
    }

    public float ratioBreakingSpeed() {
        if (biomes.contains(EBiomeType.Mountains)) {
            return isCompleteTrait() ? 0.5f : 0.33f;
        }
        return 0f;
    }

    public float bonusInfluenceMax() {
        float bonus = 0f;
        if (biomes.contains(EBiomeType.Plains))
            bonus += isCompleteTrait() ? -1750 : -850;
        if (biomes.contains(EBiomeType.Mountains))
            bonus += isCompleteTrait() ? 750 : 500;

        return bonus;
    }

    public float ratioUpgradeReduction() {
        float ratio = 0f;

        if (biomes.contains(EBiomeType.Forest)) {
            ratio += isCompleteTrait() ? -25f : -10f;
        }
        if (hasRiver) {
            ratio += isRiverComplete ? -15f : -10f;
        }
        return ratio;
    }

    private void Populate() {
        serializedBiomes.forEach(s -> biomes.add(EBiomeType.valueOf(s)));
    }

    public MutableComponent asComponent() {
        MutableComponent message = MutableComponent.create(ComponentContents.EMPTY);

        for (EBiomeType type : biomes) {
            message.append(" - ").append(type.name()).append(isCompleteTrait() ? "(Complete)" : "").append("\n");
        }
        if (hasRiver)
            message.append(" - ").append(EBiomeType.River.name()).append(isRiverComplete ? "(Complete)" : "").append("\n");

        return message;
    }

    @JsonIgnore
    public String getBiomePrefix() {
        EBiomeType type = biomes.get(0);

        switch (type) {
            case Forest -> { return "Forest"; }
            case Mountains -> { return "Mountains"; }
            case Ocean -> { return "Ocean"; }
            case Plains -> { return "Plains"; }
            case River -> { return "River"; }
            case Swamp -> { return "Swamp"; }
            case None -> { return ""; }
        }
        return "";
    }
}
