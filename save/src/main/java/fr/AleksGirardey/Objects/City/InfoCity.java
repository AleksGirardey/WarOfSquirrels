package fr.AleksGirardey.Objects.City;

import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class InfoCity {
    private static Map<Integer, Rank>   MapRanks;

    static {
        /*
        ** Faire ceci par la lecture d'un fichier config !!!
         */
        MapRanks = new HashMap<Integer, Rank>();

/*        MapColors.put(0 , TextColors.AQUA);
        MapColors.put(1 , TextColors.BLUE);
        MapColors.put(2 , TextColors.DARK_AQUA);
        MapColors.put(3 , TextColors.DARK_BLUE);
        MapColors.put(4, TextColors.DARK_GRAY);
        MapColors.put(5 , TextColors.DARK_GREEN);
        MapColors.put(6 , TextColors.DARK_PURPLE);
        MapColors.put(7 , TextColors.GRAY);
        MapColors.put(8 , TextColors.YELLOW);*/

        // Colonie < Village (< Ville) < Comté < Duché < Royaume < Empire
        // Chef < Maire (< Bourgmestre) < Comte < Duc < Roi < Empereur

        MapRanks.put(0, new Rank("Colonie", "Chef", "Assistant", 4, TextColors.BLUE));
        MapRanks.put(1, new Rank("Village", "Maire", "Assistant", 9, TextColors.AQUA));
        MapRanks.put(2, new Rank("Ville", "Bourgmestre", "Assistant", 15, TextColors.AQUA));
        MapRanks.put(3, new Rank("Comté", "Comte", "Assistant", 22, TextColors.AQUA));
        MapRanks.put(4, new Rank("Duché", "Duc", "Assistant", 30, TextColors.AQUA));
        MapRanks.put(5, new Rank("Royaume", "Roi", "Assistant", 40, TextColors.AQUA));
        MapRanks.put(6, new Rank("Empire", "Empereur", "Assistant", 50, TextColors.YELLOW));
    }

    int                     cityId;
    MutableMessageChannel   channel;
    TextColor               color;
    Rank                    rank;

    public InfoCity(int cityId, int rank) {
        this.cityId = cityId;
        this.rank = MapRanks.get(rank);
        this.color = this.rank.color;
        this.channel = null;
    }

    public void setChannel(MutableMessageChannel channel) {
        this.channel = channel;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public int getCityId() {
        return cityId;
    }

    public MutableMessageChannel getChannel() {
        return channel;
    }

    public TextColor getColor() {
        return color;
    }

    public Rank getRank() {
        return rank;
    }
}
