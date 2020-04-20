package fr.craftandconquest.warofsquirrels.object.city;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.craftandconquest.warofsquirrels.WarOfSquirrels;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class InfoCity {
    @JsonIgnore private static Map<Integer, CityRank> mapRanks;

    static {
        mapRanks = new HashMap<>();

        try {
            File configFile = new File(WarOfSquirrels.warOfSquirrelsConfigDir + "/CityRanks.properties");

            if (!configFile.exists() && !configFile.createNewFile()) {
                System.out.println("[WoS][InfoCity] Couldn't load json properties");
                System.exit(-1);
            }
            ObjectMapper mapper = new ObjectMapper();

            if (new BufferedReader(new FileReader(WarOfSquirrels.warOfSquirrelsConfigDir + "/CityRanks.properties"))
                    .readLine() == null) {
                Map<Integer, CityRank> initRanks = new HashMap<>();
                initRanks.put(0, new CityRank("Colonie", "Chef", "Assistant", 4, 2));
                initRanks.put(1, new CityRank("Village", "Maire", "Assistant", 9, 4));
                initRanks.put(2, new CityRank("Ville", "Bourgmestre", "Assistant", 15, 8));
                initRanks.put(3, new CityRank("Comté", "Comte", "Assistant", 22, 11));
                initRanks.put(4, new CityRank("Duché", "Duc", "Assistant", 30, 15));
                initRanks.put(5, new CityRank("Royaume", "Roi", "Assistant", 40, 20));
                initRanks.put(6, new CityRank("Empire", "Empereur", "Assistant", 50, 25));
            }
            mapRanks = mapper.readValue(configFile, new TypeReference<Map<Integer, CityRank>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }

    @JsonProperty @Getter @Setter private  
}
