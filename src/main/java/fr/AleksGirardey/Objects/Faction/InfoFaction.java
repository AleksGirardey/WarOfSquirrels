package fr.AleksGirardey.Objects.Faction;

import fr.AleksGirardey.Objects.City.CityRank;
import fr.AleksGirardey.Objects.DBObject.Faction;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class InfoFaction {
    private static Map<Integer, FactionRank>   MapRanks;

    static {
        MapRanks = new HashMap<>();

        MapRanks.put(0, new FactionRank("Royaume", "Roi", TextColors.AQUA));
        MapRanks.put(1, new FactionRank("Empire", "Empereur", TextColors.YELLOW));
    }

    private Faction                 faction;
    private FactionRank             rank;
    private MutableMessageChannel   channel;
    private TextColor               color;

    public InfoFaction(Faction faction) {
        this.faction = faction;
        this.rank = MapRanks.get(faction.getRank());
        this.color = this.rank.color;
        this.channel = null;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }

    public FactionRank getRank() {
        return rank;
    }

    public void setRank(FactionRank rank) {
        this.rank = rank;
    }

    public MutableMessageChannel getChannel() {
        return channel;
    }

    public void setChannel(MutableMessageChannel channel) {
        this.channel = channel;
    }

    public TextColor getColor() {
        return color;
    }

    public void setColor(TextColor color) {
        this.color = color;
    }
}