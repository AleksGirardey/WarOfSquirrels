package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public class TerritoryBiome {
    @JsonIgnore public static int riverChunkCount;
    @JsonIgnore private static final Map<Biome.BiomeCategory, EBiomeType> biomeMapping = new HashMap<>();

    static {
        /* Plains */
        biomeMapping.put(Biome.BiomeCategory.PLAINS, EBiomeType.Plains);
        biomeMapping.put(Biome.BiomeCategory.SAVANNA, EBiomeType.Plains);
        biomeMapping.put(Biome.BiomeCategory.DESERT, EBiomeType.Plains);
        biomeMapping.put(Biome.BiomeCategory.MUSHROOM, EBiomeType.Plains);

        /* Mountain */
        biomeMapping.put(Biome.BiomeCategory.EXTREME_HILLS, EBiomeType.Mountains);
        biomeMapping.put(Biome.BiomeCategory.MESA, EBiomeType.Mountains);
        biomeMapping.put(Biome.BiomeCategory.ICY, EBiomeType.Mountains);
        biomeMapping.put(Biome.BiomeCategory.MOUNTAIN, EBiomeType.Mountains);

        /* Forest */
        biomeMapping.put(Biome.BiomeCategory.JUNGLE, EBiomeType.Forest);
        biomeMapping.put(Biome.BiomeCategory.FOREST, EBiomeType.Forest);
        biomeMapping.put(Biome.BiomeCategory.TAIGA, EBiomeType.Forest);

        /* Ocean */
        biomeMapping.put(Biome.BiomeCategory.BEACH, EBiomeType.Ocean);
        biomeMapping.put(Biome.BiomeCategory.OCEAN, EBiomeType.Ocean);

        /* Swamp */
        biomeMapping.put(Biome.BiomeCategory.SWAMP, EBiomeType.Swamp);

        /* River */
        biomeMapping.put(Biome.BiomeCategory.RIVER, EBiomeType.River);

        /* None */
        biomeMapping.put(Biome.BiomeCategory.THEEND, EBiomeType.None);
        biomeMapping.put(Biome.BiomeCategory.NETHER, EBiomeType.None);
        biomeMapping.put(Biome.BiomeCategory.UNDERGROUND, EBiomeType.None);
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

    public TerritoryBiome(Map<Biome.BiomeCategory, Integer> biomeMap) {
        biomes = new ArrayList<>();
        serializedBiomes = new ArrayList<>();

        Map<EBiomeType, Integer> groupedMap = new HashMap<>();

        int max = biomeMap.values().stream().mapToInt(value -> value).sum();
        int minValueToBeComplete = 132;

        for (Map.Entry<Biome.BiomeCategory, Integer> biomeEntry : biomeMap.entrySet()) {
            EBiomeType type = biomeMapping.get(biomeEntry.getKey());

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
        MutableComponent message = new TextComponent("");

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
