package fr.AleksGirardey.Objects.City;

import org.spongepowered.api.text.format.TextColor;

public class Rank {
    String      name;
    String      prefixMayor;
    String      prefixAssistant;
    int         chunkMax;
    TextColor color;

    public Rank(
            String name,
            String prefixMayor,
            String prefixAssistant,
            int chunkMax,
            TextColor color) {
        this.name = name;
        this.prefixMayor = prefixMayor;
        this.prefixAssistant = prefixAssistant;
        this.chunkMax = chunkMax;
        this.color = color;
    }

    public String       getName() { return name; }

    public String getPrefixMayor() { return prefixMayor; }

    public String getPrefixAssistant() { return prefixAssistant; }

    public int getChunkMax() { return chunkMax; }

    public TextColor getColor() { return color; }
}
