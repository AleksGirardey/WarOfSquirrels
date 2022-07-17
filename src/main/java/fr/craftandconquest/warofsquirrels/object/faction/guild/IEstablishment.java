package fr.craftandconquest.warofsquirrels.object.faction.guild;

import java.util.List;
import java.util.UUID;

public interface IEstablishment {
    enum EstablishmentType {
        HeadQuarter,
        Branch,
        Shop,
    }

    UUID getUuid();
    EstablishmentType getEstablishmentType();
    List<IEstablishment> getSubEstablishment();
}
