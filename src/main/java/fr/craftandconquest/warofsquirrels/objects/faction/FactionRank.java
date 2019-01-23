package fr.craftandconquest.warofsquirrels.objects.faction;

import org.spongepowered.api.text.format.TextColor;

public class    FactionRank {
    private String      name;
    private String      prefixMayor;
    TextColor   color;

    FactionRank(String name, String prefix, TextColor color) {
        this.name = name;
        this.prefixMayor = prefix;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String   getPrefixMayor() { return prefixMayor; }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }
}
