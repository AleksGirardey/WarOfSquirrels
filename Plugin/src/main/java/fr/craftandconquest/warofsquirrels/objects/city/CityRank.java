package fr.craftandconquest.warofsquirrels.objects.city;

import org.spongepowered.api.text.format.TextColor;

public class    CityRank {
    String      name;
    String      prefixMayor;
    String      prefixAssistant;
    int         chunkMax;
    int         citizensMax;
    TextColor   color;

    public CityRank(
            String name,
            String prefixMayor,
            String prefixAssistant,
            int chunkMax,
            int citizensMax,
            TextColor color) {
        this.name = name;
        this.prefixMayor = prefixMayor;
        this.prefixAssistant = prefixAssistant;
        this.chunkMax = chunkMax;
        this.citizensMax = citizensMax;
        this.color = color;
    }

    public String       getName() { return name; }

    public String       getPrefixMayor() { return prefixMayor; }

    public String       getPrefixAssistant() { return prefixAssistant; }

    public int          getChunkMax() { return chunkMax; }

    public TextColor    getColor() { return color; }

    public int          getCitizensMax() { return citizensMax; }
}
