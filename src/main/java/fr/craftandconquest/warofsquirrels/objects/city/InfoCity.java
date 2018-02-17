package fr.craftandconquest.warofsquirrels.objects.city;

import fr.craftandconquest.warofsquirrels.objects.Core;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import fr.craftandconquest.warofsquirrels.objects.dbobject.City;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;

public class InfoCity {
    private static Map<Integer, CityRank>   MapRanks;

    static {
        /*
        ** TODO : Faire ceci par la lecture d'un fichier config !!!
         */
        MapRanks = new HashMap<Integer, CityRank>();

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

        MapRanks.put(0, new CityRank("Colonie", "Chef", "Assistant", 4, 2, TextColors.BLUE));
        MapRanks.put(1, new CityRank("Village", "Maire", "Assistant", 9, 4, TextColors.AQUA));
        MapRanks.put(2, new CityRank("Ville", "Bourgmestre", "Assistant", 15, 8, TextColors.AQUA));
        MapRanks.put(3, new CityRank("Comté", "Comte", "Assistant", 22, 11, TextColors.AQUA));
        MapRanks.put(4, new CityRank("Duché", "Duc", "Assistant", 30, 15, TextColors.AQUA));
        MapRanks.put(5, new CityRank("Royaume", "Roi", "Assistant", 40, 20, TextColors.AQUA));
        MapRanks.put(6, new CityRank("Empire", "Empereur", "Assistant", 50, 25, TextColors.YELLOW));
    }

    private City city;
    private MutableMessageChannel   channel;
    private TextColor               color;
    private CityRank                cityRank;

    public InfoCity(City city) {
        this.city = city;
        this.cityRank = MapRanks.get(city.getRank());
        this.color = this.cityRank.color;
        this.channel = null;
    }

    public void     setChannel(MutableMessageChannel channel) {
        this.channel = channel;
    }

    public void     setCityRank() {
        this.cityRank = MapRanks.get(city.getRank());
        this.color = cityRank.getColor();
        Core.getBroadcastHandler().cityChannel(city, "La ville a désormais le rang de '" + cityRank.getName() + "'.", TextColors.GREEN);
    }

    public City     getCity() {
        return city;
    }

    public MutableMessageChannel getChannel() {
        return channel;
    }

    public TextColor getColor() {
        return color;
    }

    public CityRank getCityRank() {
        return cityRank;
    }

    public void updateRank() {
        int rank = city.getRank();
        int citizens = city.getCitizens().size();

        if (rank < MapRanks.size() - 1) {
            if (rank != 0 && MapRanks.get(rank - 1).getCitizensMax() >= citizens) {
                city.setRank(rank - 1);
            } else if (MapRanks.get(rank + 1).getCitizensMax() <= citizens){
                city.setRank(rank + 1);
            }
        } else {
            if (MapRanks.get(rank - 1).getCitizensMax() >= citizens) {
                city.setRank(rank - 1);
            }
        }
    }
}
