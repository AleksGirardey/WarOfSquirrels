package fr.craftandconquest.warofsquirrels.object.faction;

import java.util.UUID;

public interface IFortification {
    enum FortificationType {
        CITY,
        BASTION
    }

    UUID getUniqueId();

    default int getInfluenceGenerated() {
        return 100;
    }
}
