package fr.craftandconquest.warofsquirrels.object.world;

import lombok.Getter;

import java.util.Map;

public abstract class TerritoryBiome {
    enum EBiomeType {
        Forest,
        Mountains,
        Ocean,
        Plains,
        River,
        Swamp,
    }

    @Getter
    private EBiomeType mainBiome;
    @Getter
    private EBiomeType[] specialBiomes;

    protected TerritoryBiome(Map<Integer, Integer> biomeMap) {

    }
}
