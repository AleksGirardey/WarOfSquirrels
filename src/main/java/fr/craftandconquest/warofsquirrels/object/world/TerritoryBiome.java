package fr.craftandconquest.warofsquirrels.object.world;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class TerritoryBiome {
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

        /* Forest */
        biomeMapping.put(Biome.BiomeCategory.JUNGLE, EBiomeType.Forest);
        biomeMapping.put(Biome.BiomeCategory.FOREST, EBiomeType.Forest);

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

    @JsonProperty @Getter private List<EBiomeType> biomes;
    @JsonProperty @Getter private boolean hasRiver = false;
    @JsonProperty @Getter private boolean isRiverComplete = false;

    public TerritoryBiome(Map<Biome.BiomeCategory, Integer> biomeMap) {
        biomes = new ArrayList<>();

        Map<EBiomeType, Integer> groupedMap = new HashMap<>();

        int max = biomeMap.values().stream().mapToInt(value -> value).sum();
        int minValueToBeComplete = 10;

        for (Map.Entry<Biome.BiomeCategory, Integer> biomeEntry : biomeMap.entrySet()) {
            EBiomeType type = biomeMapping.get(biomeEntry.getKey());

            groupedMap.compute(type, (k, v) -> v == null ? 1 : v + 1);
        }

        if (groupedMap.containsKey(EBiomeType.River)) {
            hasRiver = true;
            isRiverComplete = groupedMap.get(EBiomeType.River) >= minValueToBeComplete;
            groupedMap.remove(EBiomeType.River);
        }

        groupedMap.forEach((k, v) -> v = v * 100 / max);
        groupedMap.entrySet().stream()
                .filter(e -> e.getValue() >= 33 && e.getKey() != EBiomeType.None)
                .forEach(e -> addBiome(e.getKey()));
    }

    private void addBiome(EBiomeType type) {
        biomes.add(type);
    }

    @JsonIgnore
    private boolean isCompleteTrait() { return biomes.size() == 1; }

    public float ratioBonusInfluenceOnMe() {
        if (biomes.contains(EBiomeType.Plains)) {
            return isCompleteTrait() ? 1.5f : 2f;
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
        if (biomes.contains(EBiomeType.Plains))
            bonus += isCompleteTrait() ? 750 : 500;

        return bonus;
    }

    public float ratioUpgradeReduction() {
        float ratio = 0f;

        if (biomes.contains(EBiomeType.Forest)) {
            ratio += isCompleteTrait() ? 40f : 20f;
        }
        if (hasRiver) {
            ratio += isRiverComplete ? 10f : 15f;
        }
        return ratio;
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
}
