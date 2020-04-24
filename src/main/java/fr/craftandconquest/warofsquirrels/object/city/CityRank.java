package fr.craftandconquest.warofsquirrels.object.city;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
public class CityRank {
    @Getter @Setter String      name;
    @Getter @Setter String      prefixMayor;
    @Getter @Setter String      prefixAssistant;
    @Getter @Setter int         chunkMax;
    @Getter @Setter int         citizensMax;
}
